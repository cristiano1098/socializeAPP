package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Expense.Expense
import com.example.cmu_g10.Data.Expense.ExpenseEvent
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import com.example.cmu_g10.floatingActionButton.FabButtonItem
import com.example.cmu_g10.floatingActionButton.FabButtonMain
import com.example.cmu_g10.floatingActionButton.FabButtonSub
import com.example.cmu_g10.floatingActionButton.MultiFloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

/**
 * Composable function representing the "GroupExpenseScreen."
 * Displays a group expense screen using the [Scaffold] and [ModalNavigationDrawer] components.
 *
 * @param groupId The id of the group.
 * @param navController The navigation controller for handling navigation within the app.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param onEvent The event handler for the expense.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupExpenseScreen(
    groupId: Int,
    navController: NavController,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    onEvent: (ExpenseEvent) -> Unit,
) {
    val context = LocalContext.current
    val expenses by expenseViewModel.groupExpenses.observeAsState(initial = emptyList())

    expenseViewModel.fetchGroupExpenses(groupId)
    // Main content of the screen
    Scaffold(containerColor = Color.White, topBar = {
        val topAppBarState = rememberTopAppBarState()
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Socialize",
                        fontSize = 21.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // Display dropdown menu with group-related options
                DropdownGrupos(navController, groupId, groupViewModel, userViewModel)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }, floatingActionButton = {
        // Display a custom multi-float action button
        MultiFloatingActionButton(
            modifier = Modifier.padding(15.dp), items = listOf(
                FabButtonItem(
                    iconRes = Icons.Filled.Edit, label = "Gerir Membros"
                ), FabButtonItem(
                    iconRes = Icons.Filled.Add, label = "Adicionar Despesa"
                )
            ), onFabItemClicked = {
                // Handle the click on each action button
                if (it.label == "Adicionar Despesa") {
                    navController.navigate("addExpense/$groupId")
                    Toast.makeText(context, "Adicionar Despesa", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("manageGroupMembers/$groupId")
                    Toast.makeText(context, "Gerir Membros", Toast.LENGTH_SHORT).show()
                }
            }, fabIcon = FabButtonMain(), fabOption = FabButtonSub()
        )
    }) { innerPadding ->
        // Main content layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Display group picture and name
            Row(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.Center
            ) {
                GroupPicAndName(groupId, groupViewModel)
            }
            // Display group balance information
            Row(
                modifier = Modifier
                    .weight(0.08f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.Center,
            ) {
                SaldoGrupo(
                    expenseViewModel,
                    userViewModel.loggedUserData.value?.userId ?: 0,
                    groupId
                )
            }
            // Display button to settle debts
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomEnd = 45.dp))
                    .weight(0.07f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButonSettleDebt(navController, groupId)
            }
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.7f)
                    .fillMaxWidth()
                    .padding(top = 10.dp)

            ) {
                items(expenses) { expense ->
                    if (expense != null) {
                        GroupOperationsCard(
                            navController,
                            expense,
                            userViewModel,
                            onEvent,
                            expenseViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function for a button to settle debts.
 *
 * @param navController The NavController used for navigating between screens.
 * @param groupId The id of the group.
 */
@Composable
fun ButonSettleDebt(navController: NavController, groupId: Int?) {
    val context = LocalContext.current
    Button(
        onClick = {
            navController.navigate("settleBalance/$groupId")
            Toast.makeText(context, "Acertar Contas", Toast.LENGTH_SHORT)
                .show()
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(Color(0xff301934)),
        modifier = Modifier
            .height(20.dp)
            .width(100.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            "Acertar Contas",
            color = Color.White,
            fontSize = 12.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Function for formatting the date.
 *
 * @param date The date to be formatted.
 */
fun formatDate(date: String): String {
    val originalFormat = SimpleDateFormat("d/M/yyyy", Locale.US)
    val targetFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    val originalDate = originalFormat.parse(date)
    return if (originalDate != null) {
        targetFormat.format(originalDate)
    } else {
        "Invalid Date"
    }
}


/**
 * Composable function for the group operations card.
 *
 * @param navController The NavController used for navigating between screens.
 * @param expense The expense to be displayed.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param onEvent The event handler for the expense.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 */
@Composable
fun GroupOperationsCard(
    navController: NavController,
    expense: Expense,
    userViewModel: UserViewModel,
    onEvent: (ExpenseEvent) -> Unit,
    expenseViewModel: ExpenseViewModel,
) {
    val context = LocalContext.current
    Card(
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .padding(vertical = 5.dp)
            .padding(horizontal = 20.dp),
    ) {
        GroupOperationsCardContent(navController, expense, userViewModel, expenseViewModel)
    }
}

/**
 * Composable function for the content of the group operations card.
 *
 * @param navController The NavController used for navigating between screens.
 * @param expense The expense to be displayed.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 */
@Composable
fun GroupOperationsCardContent(
    navController: NavController,
    expense: Expense,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
) {
    val loggedUserId = userViewModel.loggedUserData.value?.userId
    val userInfo by userViewModel.getUserInfo(expense.paidByUserId).observeAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val participants by expenseViewModel.getExpenseParticipantsById(expense.expenseId)
        .observeAsState(initial = emptyList())
    val amountOwedOrOwing = calculateAmountOwedOrOwing(participants, loggedUserId)

    // Determine the text based on the amount
    val amountText = when {
        amountOwedOrOwing > 0 -> "Deves"// User owes money
        amountOwedOrOwing < 0 -> "Devem-te" // User is owed money
        else -> "Settled" // No money owed or to be owed
    }

    val amountTextColor = when {
        amountOwedOrOwing > 0 -> Color.Red // Usuário deve dinheiro
        amountOwedOrOwing < 0 -> Color.Green // Usuário tem dinheiro a receber
        else -> MaterialTheme.colorScheme.onBackground // Nenhum dinheiro devido ou a receber
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar remoção") },
            text = { Text("Tem certeza de que deseja remover ${expense.description}?") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        expenseViewModel.deleteExpense(expense.expenseId, expense.groupId)
                        Toast.makeText(
                            context,
                            "Removeste ${expense.description} do grupo",
                            Toast.LENGTH_SHORT
                        ).show()
                        showConfirmationDialog = false
                    }
                )
                {
                    Text("Feito", color = Color.White)
                }
            },
            dismissButton = {
                androidx.compose.material3.Button(
                    onClick = { showConfirmationDialog = false },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(
                            0xff8b0000
                        )
                    )
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
    Row(
        modifier = Modifier
            .size(400.dp, 100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.52f),
            horizontalAlignment = Alignment.Start,
        ) {
            // Display the shopping cart icon
            Image(
                imageVector = Icons.TwoTone.ShoppingCart,
                contentDescription = "Icon",
                modifier = Modifier
                    .size(70.dp, 50.dp)
                    .padding(top = 10.dp)
                    .padding(horizontal = 20.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            // Display the group name and added by information
            Text(
                text = expense.description,
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Adicionado por ${userInfo?.name}",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp),
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .padding(bottom = 55.dp)
        ) {
            IconButton(onClick = {
                navController.navigate("editExpense/${expense.groupId}/${expense.expenseId}")
                Toast.makeText(context, "Editar Despesa", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF32209B),
                    modifier = Modifier
                        .padding(start = 7.dp)
                        .background(Color.White, CircleShape),
                )
            }
            IconButton(onClick = {
                showConfirmationDialog = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove",
                    modifier = Modifier.background(Color.White, CircleShape),
                    tint = Color(0xFFB10B0B),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 10.dp, bottom = 10.dp),
        ) {
            // Display date, payer, and amount information
            Text(
                text = formatDate(expense.dateOfExpense),
                modifier = Modifier.padding(end = 15.dp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = amountText,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .padding(top = 10.dp),
                fontSize = 15.sp,
                color = amountTextColor,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "${String.format("%.2f", abs(amountOwedOrOwing))}€",
                modifier = Modifier.padding(end = 15.dp),
                fontSize = 22.sp,
                color = amountTextColor,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/**
 * Function for calculating the amount owed or owing.
 *
 * @param participants The list of participants in the expense.
 * @param loggedUserId The id of the logged user.
 */
fun calculateAmountOwedOrOwing(
    participants: List<ExpenseParticipants>,
    loggedUserId: Int?
): Double {
    var amountOwedOrOwing = 0.0
    for (participant in participants) {
        if (participant.userId == loggedUserId) {
            amountOwedOrOwing = participant.owedAmount
        }
    }
    return amountOwedOrOwing
}


/**
 * Composable function for the group picture and name.
 *
 * @param groupId The id of the group.
 * @param viewModel The [GroupViewModel] that holds the state for the group.
 */
@Composable
fun GroupPicAndName(groupId: Int, viewModel: GroupViewModel) {
    LaunchedEffect(groupId) {
        viewModel.fetchGroupDetails(groupId)
    }

    val group by viewModel.groupDetails.observeAsState()
    val profilePicturePainter = painterResource(id = R.drawable.socialize_logo)

    group?.let {
        Column(
            modifier = Modifier
                .padding(top = 52.dp)
                .size(300.dp, 130.dp)
                .fillMaxWidth(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display the group picture (placeholder) and name
            Image(
                painter = profilePicturePainter,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
            )
            Text(
                text = it.name,
                modifier = Modifier.padding(top = 5.dp),
                fontSize = 15.sp,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Composable function for the group balance information.
 *
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param userId The id of the user.
 * @param groupId The id of the group.
 */
@Composable
fun SaldoGrupo(expenseViewModel: ExpenseViewModel, userId: Int, groupId: Int) {
    val amountOwedByUserState =
        expenseViewModel.getAmountOwedByUserInGroup(userId, groupId).observeAsState()
    val amountOwedToUserState =
        expenseViewModel.getAmountOwedToUserInGroup(userId, groupId).observeAsState()
    val netBalanceState =
        expenseViewModel.getUserNetBalanceInGroup(userId, groupId).observeAsState()

    val amountOwedByUser = amountOwedByUserState.value ?: 0.0
    val amountOwedToUser = amountOwedToUserState.value ?: 0.0
    val netBalance = netBalanceState.value ?: 0.0

    val netBalanceColor = if (netBalance >= 0) Color(0xFF006400) else Color.Red

    // Display a card with group balance information
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        border = BorderStroke(0.5.dp, Color(0xff301934)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Column 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Deves",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = String.format("%.2f€", amountOwedByUser),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(2.dp)
                    .padding(horizontal = 0.1.dp)
            )

            // Column 2
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Devem-te",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = String.format("%.2f€", amountOwedToUser),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(2.dp)
                    .padding(horizontal = 0.1.dp)
            )

            // Column 3
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Saldo Total",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = String.format("%.2f€", netBalance),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = netBalanceColor,
                )
            }
        }
    }
}

/**
 * Composable function for a dropdown menu with group-related options.
 *
 * This composable function creates a dropdown menu that provides group-related actions,
 * such as editing the group, viewing reports, leaving the group, and logging out.
 *
 * @param navController The NavController used for navigating between screens.
 * @param groupId The id of the group.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun DropdownGrupos(
    navController: NavController,
    groupId: Int,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Display a button with a dropdown menu for group-related actions
    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White
        )
    }
    Box(
        modifier = Modifier
            .padding(end = 5.dp)
            .padding(top = 40.dp)
            .background(Color.White),
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(onClick = {
                navController.navigate("editGroup/$groupId")
                expanded = false
                Toast.makeText(context, "Editar Grupo", Toast.LENGTH_SHORT).show()
            }) {
                Text(
                    text = "Editar Grupo", style = MaterialTheme.typography.labelSmall
                )
            }
            DropdownMenuItem(onClick = {
                navController.navigate("chartScreen")
                expanded = false
                Toast.makeText(context, "Relatórios", Toast.LENGTH_SHORT).show()
            }) {
                Text(
                    text = "Relatórios", style = MaterialTheme.typography.labelSmall
                )
            }
            if (showConfirmationDialog) {
                AlertDialog(onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Confirmar saída") },
                    text = { Text("Tem certeza de que deseja sair do grupo?") },
                    confirmButton = {
                        Button(onClick = {
                            val userId = userViewModel.loggedUserData.value?.userId
                            if (userId != null) {
                                expanded = false
                                groupViewModel.onEvent(
                                    GroupEvent.RemoveUserFromGroup(
                                        groupId, userId
                                    )
                                )
                                navController.navigate("homeScreen")
                                Toast.makeText(context, "Saiu do Grupo", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro ao sair do Grupo", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            showConfirmationDialog = false
                        }) {
                            Text("Cancelar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showConfirmationDialog = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff941616))
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                    })
            }
            DropdownMenuItem(onClick = {
                showConfirmationDialog = true
            }) {
                Text(
                    text = "Sair do Grupo",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xff941616)
                )
            }
            DropdownMenuItem(onClick = {
                navController.navigate("login/Register")
                expanded = false
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            }) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xff941616)
                )
            }
        }
    }
}

