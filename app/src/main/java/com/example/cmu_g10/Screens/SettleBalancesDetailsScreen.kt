package com.example.cmu_g10.Screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cmu_g10.Services.Camara.CamaraActivity
import com.example.cmu_g10.Data.Expense.ExpenseState
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Services.Notifications.NotificationsService
import com.example.cmu_g10.Data.Payment.Payment
import com.example.cmu_g10.Data.Payment.PaymentViewModel
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R

/**
 * Composable for the Settle Balances Details screen.
 *
 * @param navController NavHostController for navigation.
 * @param expenseId Id of the expense.
 * @param expenseViewModel [ExpenseViewModel] that holds the state for the expense.
 * @param userViewmodel [UserViewModel] that holds the state for the user.
 * @param owedAmount Amount owed by the user.
 * @param paymentViewModel [PaymentViewModel] that holds the state for the payment.
 */
@Composable
fun SettleBalancesDetailsScreen(
    navController: NavHostController,
    expenseId: Int?,
    expenseViewModel: ExpenseViewModel,
    userViewmodel: UserViewModel,
    owedAmount: Double?,
    paymentViewModel: PaymentViewModel
) {
    Scaffold(
        topBar = { SettleBalancesDetailsTopBar(navController) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                SettleAccountsDetailsContent(
                    userViewmodel,
                    owedAmount,
                    navController,
                    expenseViewModel,
                    expenseId,
                    paymentViewModel
                )
            }
        }
    )
}

/**
 * Composable for the top app bar of Settle Balances Details screen.
 *
 * @param navController NavHostController for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleBalancesDetailsTopBar(navController: NavHostController) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        title = {
            Text(
                "Acertar Contas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    )
}

/**
 * Composable for the content of Settle Balances Details screen.
 *
 * @param userViewmodel [UserViewModel] that holds the state for the user.
 * @param owedAmount Amount owed by the user.
 * @param navController NavHostController for navigation.
 * @param expenseViewModel [ExpenseViewModel] that holds the state for the expense.
 * @param expenseId Id of the expense.
 * @param paymentViewModel [PaymentViewModel] that holds the state for the payment.
 */
@Composable
fun SettleAccountsDetailsContent(
    userViewmodel: UserViewModel,
    owedAmount: Double?,
    navController: NavHostController,
    expenseViewModel: ExpenseViewModel,
    expenseId: Int?,
    paymentViewModel: PaymentViewModel

) {
    val loggedUser = userViewmodel.loggedUserData.value?.userId ?: 0
    val formattedText = String.format("%.2f €", owedAmount)
    val expenseState by expenseViewModel.expenseDetails.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(expenseId) {
        expenseId?.let { expenseViewModel.getExpense(it) }
    }

    LaunchedEffect(expenseState?.paidByUserId) {
        expenseState?.paidByUserId.let {
            if (it != null) {
                userViewmodel.getUser(it)
            }
        }
    }
    val userState by userViewmodel.userData.observeAsState()

    val address = expenseState?.location ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    append("Acertar conta com o ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    userState?.let { append(it.name) }
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(15.dp))

        TypeIcon(type = "Outros")

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = expenseState?.dateOfExpense.toString(),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = formattedText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        FileInput(onClick = {
            val intent = Intent(context, CamaraActivity::class.java)
            context.startActivity(intent)
        })

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    append("Pago por ti a ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    userState?.let { append(it.name) }
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = {
                navController.navigate("map/$address")
            }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Localização", modifier = Modifier.padding(end = 8.dp), color = Color.White)
        }

        SettleButton {
            if (owedAmount != null) {
                expenseState?.let {
                    expenseViewModel.updateAmountUserOwes(
                        it.paidByUserId, loggedUser,
                        it.expenseId, owedAmount
                    )
                    paymentViewModel.recordPayment(
                        it.expenseId, loggedUser, it.paidByUserId, owedAmount
                    )

                    //Sends a settled expense notification
                    val notificationsService = NotificationsService(context)
                    notificationsService.showNotification(
                        title = "Conta Acertada",
                        text = "Você acertou a conta com ${userState?.name}. Valor: $owedAmount€"
                    )
                }
            }
            navController.navigate("SuccessScreen/${userState?.name}")
        }
    }
}

/**
 * Composable for displaying the transaction type icon and name.
 *
 * @param type Type of transaction.
 */
@Composable
fun TypeIcon(type: String) {
    val typeIcon = when (type) {
        "Car" -> R.drawable.outline_directions_car_24 to "Carro"
        "Restaurant" -> R.drawable.outline_restaurant_24 to "Restaurante"
        "Groceries" -> R.drawable.outline_shopping_basket_24 to "Compras"
        "Gas" -> R.drawable.outline_local_gas_station_24 to "Combustível"
        "Food" -> R.drawable.outline_fastfood_24 to "Comida"
        "Travel" -> R.drawable.outline_airplanemode_active_24 to "Viagem"
        else -> R.drawable.outline_shopping_bag_24 to "Outros"
    }

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = Color.LightGray,
            )
            .background(MaterialTheme.colorScheme.secondary)
            .padding(10.dp)
            .size(112.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = typeIcon.first),
            contentDescription = "${type} Icon",
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .scale(1.5f),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = typeIcon.second,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary),
        )
    }
}

/**
 * Composable for the Settle button.
 *
 * @param onClick Lambda function to define the action when the button is clicked.
 */
@Composable
fun SettleButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Acertar",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * Composable for the smaller file input without border.
 *
 * @param onClick Lambda function to define the action when the button is clicked.
 * @param modifier Modifier for the Composable.
 */
@Composable
fun FileInput(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Comprovativo",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                painterResource(id = R.drawable.baseline_camera_alt_24),
                contentDescription = "Camera Icon",
                modifier = Modifier
                    .size(36.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}


