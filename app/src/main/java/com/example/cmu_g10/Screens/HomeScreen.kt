package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Expense.Expense
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.Group.Group
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupState
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Data.Payment.Payment
import com.example.cmu_g10.Data.Payment.PaymentViewModel
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import com.example.cmu_g10.floatingActionButton.FabButtonItem
import com.example.cmu_g10.floatingActionButton.FabButtonMain
import com.example.cmu_g10.floatingActionButton.FabButtonSub
import com.example.cmu_g10.floatingActionButton.MultiFloatingActionButton
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.reflect.KFunction1

/**
 * Composable function representing the main screen of the application.
 *
 * This screen contains a navigation drawer, top app bar, and main content.
 * The main content includes user information, group balances, and tabs for different sections.
 *
 * @param navController The NavController used for navigating between screens.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param paymentViewModel The [PaymentViewModel] that holds the state for the payment.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    expenseViewModel: ExpenseViewModel,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val groups by groupViewModel.userGroups.observeAsState(initial = emptyList())

    groupViewModel.fetchGroupsForUser(userViewModel.loggedUserData.value?.userId ?: 0)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp),
            ) {
                Text(
                    "Grupos",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 15.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(9.dp)
                ) {
                    items(groups) { group ->
                        SeeGroupCard(navController, group = group)
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = {
                val topAppBarState = rememberTopAppBarState()
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu, contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        DropdownHomescreen(navController, userViewModel)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            },
            floatingActionButton = {
                MultiFloatingActionButton(
                    modifier = Modifier
                        .padding(15.dp),
                    items = listOf(
                        FabButtonItem(
                            iconRes = Icons.Filled.Check,
                            label = "Acertar Contas"
                        ),
                        FabButtonItem(
                            iconRes = Icons.Filled.Add,
                            label = "Criar Grupo"
                        )
                    ),
                    onFabItemClicked = {
                        if (it.label == "Acertar Contas") {
                            navController.navigate("settleBalance")
                            Toast.makeText(context, "Acertar Contas", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            navController.navigate("createGroup")
                            Toast.makeText(context, "Criar Novo Grupo", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    fabIcon = FabButtonMain(),
                    fabOption = FabButtonSub()
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    UserPicAndName(userViewModel)
                }
                Row(
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SaldoGeral(expenseViewModel, userViewModel.loggedUserData.value?.userId ?: 0)
                }
                Row(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    HomescreenTab(
                        navController,
                        expenseViewModel,
                        userViewModel,
                        groupViewModel,
                        paymentViewModel
                    )
                }
            }
        }
    }
}

/**
 * Composable function to display a card representing a group in the UI.
 *
 * @param navController The NavController used for navigating between screens.
 * @param group The group object containing information about the group.
 */
@Composable
fun SeeGroupCard(navController: NavController, group: Group) {
    val context = LocalContext.current
    Card(
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(MaterialTheme.colorScheme.background),
        onClick = {
            navController.navigate("group/${group.groupId}")
            Toast.makeText(context, "Grupo ${group.name}", Toast.LENGTH_SHORT).show()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp)

                    .fillMaxWidth(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_picture),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Composable function representing a tabbed layout for the home screen.
 *
 * @param navController The NavController used for navigation between screens.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param paymentViewModel The [PaymentViewModel] that holds the state for the payment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomescreenTab(
    navController: NavController,
    expenseViewModel: ExpenseViewModel,
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    paymentViewModel: PaymentViewModel
) {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Despesas", "Pagamentos")
    Column {
        PrimaryTabRow(
            modifier = Modifier.clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
            selectedTabIndex = state,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[state]),
                    color = MaterialTheme.colorScheme.primary
                )
            },

            ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary),
                    selected = state == index,
                    onClick = { state = index },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (state == 0)
                GroupCard(navController, userViewModel, expenseViewModel, groupViewModel)
            else
                ActivityCard(paymentViewModel, userViewModel)
        }
    }
}

/**
 * Composable function representing a card displaying an activity.
 *
 * @param paymentViewModel The [PaymentViewModel] that holds the state for the payment.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun ActivityCard(paymentViewModel: PaymentViewModel, userViewModel: UserViewModel) {

    val loggedUser = userViewModel.loggedUserData.value?.userId ?: 0
    val payments by paymentViewModel.fetchUserPayments(loggedUser)
        .observeAsState(initial = emptyList())

    LazyColumn {
        items(payments) { payment ->
            ActivityCardContent(payment, loggedUser, userViewModel)
        }
    }
}

/**
 * Composable function representing a card displaying group information.
 *
 * @param navController The NavController used for navigation.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 */
@Composable
fun GroupCard(
    navController: NavController,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel
) {
    val loggedInUser = userViewModel.loggedUserData.value?.userId ?: 0
    val expenses by expenseViewModel.getExpensesForUser(loggedInUser)
        .observeAsState(initial = emptyList())

    LazyColumn {
        items(expenses) { expense ->
            GroupCardContent(
                expense,
                expenseViewModel,
                userViewModel,
                navController,
                groupViewModel
            )
        }
    }
}

/**
 * Composable function representing the content of an activity card.
 *
 * @param payment The [Payment] object containing information about the payment.
 * @param loggedUserId The id of the logged in user.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun ActivityCardContent(
    payment: Payment,
    loggedUserId: Int,
    userViewModel: UserViewModel
) {
    val userLiveData = userViewModel.getUserLiveData(payment.payerUserId)
    val user by userLiveData.observeAsState()

    val firstMessage = if (payment.payerUserId == loggedUserId) {
        "Acertestaste conta com " + user?.name
    } else {
        "O " + user?.name + " acertou contas contigo"
    }

    val secondMessage = if (payment.payerUserId == loggedUserId) {
        "Pagaste"
    } else {
        user?.name + " pagou"
    }

    val formattedAmount = String.format("%.2f€", payment.amount)

    Card(
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = firstMessage,
                    modifier = Modifier
                        .padding(5.dp),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "$secondMessage $formattedAmount",
                    modifier = Modifier
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

/**
 * Composable function representing the content of a group card.
 *
 * @param expense The [ExpenseParticipants] object containing information about the expense.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param navController The NavController used for navigation.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 */
@Composable
fun GroupCardContent(
    expense: ExpenseParticipants?,
    expenseViewModel: ExpenseViewModel,
    userViewModel: UserViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    val expenseLiveData = expenseViewModel.getExpenseLiveData(expense?.expenseId ?: 0)
    val expenseState by expenseLiveData.observeAsState()

    val userLiveData = userViewModel.getUserLiveData(expenseState?.paidByUserId ?: 0)
    val user by userLiveData.observeAsState()

    val groupLiveData = groupViewModel.getGroupLiveData(expense?.groupId ?: 0)
    val groupDetails by groupLiveData.observeAsState()

    val userLogged = userViewModel.loggedUserData.observeAsState()
    val userInfo = userViewModel.getUserInfo(expenseState?.paidByUserId ?: 0).observeAsState()

    if (userInfo.value?.name == userLogged.value?.name) {
        userInfo.value?.name = "mim"
    }

    val amountOwedOrOwing = expense?.owedAmount ?: 0.00

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
    Card(
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            navController.navigate("group/${expense?.groupId}")
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Image column
            Column(
                modifier = Modifier
                    .padding(end = 16.dp)
            ) {
                Image(
                    imageVector = Icons.TwoTone.AddCircle,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .padding(4.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                )
            }

            // Text content column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                if (expense != null) {
                    Text(
                        text = expenseState?.description ?: "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                if (expense != null) {
                    Text(
                        text = "Adicionado por ${userInfo.value?.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (expense != null) {
                    Text(
                        text = "Grupo ${groupDetails?.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            // Additional information column
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = amountText,
                    modifier = Modifier
                        .padding(end = 15.dp),
                    fontSize = 15.sp,
                    color = amountTextColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "${String.format("%.2f", abs(amountOwedOrOwing))}€",
                    modifier = Modifier.padding(end = 15.dp),
                    fontSize = 20.sp,
                    color = amountTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

/**
 * Composable function representing the user profile picture and name.
 *
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun UserPicAndName(userViewModel: UserViewModel) {
    val profilePicturePainter = painterResource(id = R.drawable.socialize_logo)
    val user by userViewModel.loggedUserData.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .padding(top = 75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = profilePicturePainter,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(5.dp))

        user?.let { user ->
            Text(
                text = user.name,
                fontSize = 15.sp,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Composable function representing the overall balance information.
 *
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param userId The id of the user.
 */
@Composable
fun SaldoGeral(expenseViewModel: ExpenseViewModel, userId: Int = 0) {
    val owedToUserAmountState = expenseViewModel.getAmountOwedToUser(userId).observeAsState()
    val owedByUserAmountState = expenseViewModel.getAmountUserOwes(userId).observeAsState()
    val netBalanceState = expenseViewModel.getUserNetBalance(userId).observeAsState()

    // Use default values if LiveData emits null
    val owedByUserAmount = owedByUserAmountState.value ?: 0.00
    val owedToUserAmount = owedToUserAmountState.value ?: 0.00
    val netBalance = netBalanceState.value ?: 0.00

    // Format the values
    val formattedBalance = String.format("%.2f€", netBalance)
    val formattedOwedByUser = String.format("%.2f€", owedByUserAmount)
    val formattedOwedToUser = String.format("%.2f€", owedToUserAmount)

    val color = if (formattedBalance < 0.toString()) Color.Red else Color(0xFF006400)

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        border = BorderStroke(0.5.dp, Color(0xff301934)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Row containing three columns representing different balance details
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Column 1 - Amount owed by the user
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Deves",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = formattedOwedByUser,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }

            // Divider between columns
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(2.dp)
                    .padding(horizontal = 0.1.dp)
            )

            // Column 2 - Amount owed to the user
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Devem-te",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = formattedOwedToUser,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }

            // Divider between columns
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(2.dp)
                    .padding(horizontal = 0.1.dp)
            )

            // Column 3 - Overall total balance
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Saldo Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = formattedBalance,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}

/**
 * Composable function representing a dropdown menu for home screen actions.
 *
 * @param navController The NavController for navigating to different destinations.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun DropdownHomescreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            tint = Color.White
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
            DropdownMenuItem(
                onClick = {
                    navController.navigate("editProfile")
                    expanded = false
                    Toast.makeText(context, "Editar Perfil", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "Editar Perfil",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            DropdownMenuItem(onClick = {
                navController.navigate("login/Register")
                expanded = false
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                userViewModel.logoutUser()
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
