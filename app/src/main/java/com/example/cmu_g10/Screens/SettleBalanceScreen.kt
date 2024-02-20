package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Expense.Expense
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.Payment.PaymentViewModel
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserViewModel
import kotlin.math.abs

/**
 * Composable function representing the Settle Balances screen.
 *
 * This function sets up the top app bar and content for the Settle Balances screen.
 *
 * @param navController NavController for managing navigation within the app.
 * @param userViewmodel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 */
@Composable
fun SettleBalanceScreen(
    navController: NavController,
    userViewmodel: UserViewModel,
    expenseViewModel: ExpenseViewModel
) {
    EditProfileTopBar(navController, userViewmodel, expenseViewModel)
}

/**
 * Composable function representing the Settle Balances screen.
 *
 * This function sets up the top app bar and content for the Settle Balances screen.
 *
 * @param navController NavController for managing navigation within the app.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun EditProfileTopBar(
    navController: NavController,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel
) {
    val loggedUser = userViewModel.loggedUserData.value?.userId ?: 0
    val usersAndAmountsOwed by expenseViewModel.getAmountsUserOwes(loggedUser)
        .observeAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        "Acertar Contas",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
            )
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = 70.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Com quem queres acertar contas?",
                modifier = Modifier
                    .padding(15.dp)
                    .padding(horizontal = 10.dp),
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp)
                .fillMaxSize(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            usersAndAmountsOwed?.let {
                items(it.size) { index ->
                    val userAmount = usersAndAmountsOwed?.get(index)
                    if (userAmount != null) {
                        MemberCard(navController, userAmount, userViewModel, expenseViewModel)
                    }
                }
            }
        }
    }
}

/**
 * Composable function representing a member card in the Settle Balances screen.
 *
 * This function creates an OutlinedCard with member information and handles click events.
 *
 * @param navController NavController for managing navigation within the app.
 * @param userAmount The [ExpenseParticipants] that holds the state for the user.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 */
@Composable
fun MemberCard(
    navController: NavController,
    userAmount: ExpenseParticipants,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
) {
    val expenseLiveData = expenseViewModel.getExpenseLiveData(userAmount.expenseId)
    val expense by expenseLiveData.observeAsState()

    val userLiveData = userViewModel.getUserLiveData(expense?.paidByUserId ?: 0)
    val user by userLiveData.observeAsState()

    Card(
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(horizontal = 20.dp)
            .size(400.dp, 100.dp)
            .clickable { navController.navigate("settleBalanceDetail/${userAmount.expenseId}/${userAmount.owedAmount}") },
    ) {
        MemberCardContent(
            userAmount, user, expense
        )
    }
}


/**
 * Composable function representing the content of a member card.
 *
 * This function displays information about a member, including name, email, and amount owed.
 *
 * @param userAmount The [ExpenseParticipants] that holds the state for the user.
 * @param user The [User] that holds the state for the user.
 * @param expense The [Expense] that holds the state for the expense.
 */
@Composable
fun MemberCardContent(
    userAmount: ExpenseParticipants,
    user: User?,
    expense: Expense?
) {
    Row(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = Icons.TwoTone.AddCircle,
            contentDescription = "Icon",
            modifier = Modifier
                .size(100.dp, 100.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Column(
            modifier = Modifier
                .padding(5.dp)
                .size(150.dp, 400.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let {
                Text(
                    text = it.name,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
            user?.let {
                Text(
                    text = it.email,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                text = "Expense: ${
                    expense?.let {
                        it.description
                    } ?: "N/A"
                }",
                modifier = Modifier
                    .padding(horizontal = 5.dp),
                fontSize = 10.sp,
                color = Color(0xFFBDBDBD),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Column(
            modifier = Modifier
                .padding(5.dp)
                .size(150.dp, 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Deves",
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
            Text(
                text = "${String.format("%.2f", userAmount.owedAmount)}€",
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }
    }
}
