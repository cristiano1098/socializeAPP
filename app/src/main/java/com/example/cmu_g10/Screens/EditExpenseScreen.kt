package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Expense.ExpenseEvent
import com.example.cmu_g10.Data.Expense.ExpenseState
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection

/**
 * Composable function representing the "EditExpenseScreen."
 * Displays an editable expense screen using the [Scaffold] component for overall layout structure.
 *
 * @param groupId The id of the group.
 * @param navController The navigation controller for handling back navigation.
 * @param state The [ExpenseState] that holds the state for the expense.
 * @param onEvent The event to be triggered.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param expenseViewModel The [ExpenseViewModel] that holds the state for the expense.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param expenseId The id of the expense.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditExpenseScreen(
    groupId: Int,
    navController: NavController,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel,
    expenseId: Int
) {
    val showDialogState = remember { mutableStateOf(false) }
    val usersList by userViewModel.allUsers.observeAsState()
    val currentUser by userViewModel.loggedUserData.observeAsState()
    val groupMembers by groupViewModel.groupMembers.observeAsState()
    val context = LocalContext.current
    val expenseLiveData = expenseViewModel.getExpenseLiveData(expenseId)
    val expense by expenseLiveData.observeAsState()

    var description by remember(expense) { mutableStateOf(expense?.description ?: "") }
    var location by remember(expense) { mutableStateOf(expense?.location ?: "") }
    var amount by remember(expense) { mutableStateOf(expense?.amount ?: "") }
    var selectedDate by remember(expense) { mutableStateOf(expense?.dateOfExpense ?: "") }

    val autocompleteResults by expenseViewModel.autocompleteResults.observeAsState(initial = emptyList())

    LaunchedEffect(expenseId) {
        expenseViewModel.clearAutocompleteResults()
    }

    val locationQuery = remember { mutableStateOf("") }

    val showLazyColumn = remember { mutableStateOf(true) }

    val maxHeight = 300.dp
    val itemHeight = 45.dp // Approximate height of each item
    val dynamicHeight = dynamicLazyColumnHeight(autocompleteResults.size, maxHeight, itemHeight)

    val calendarState = rememberUseCaseState()

    val selectedUsersState = remember { mutableStateOf<List<User>>(listOf()) }

    LaunchedEffect(groupId) {
        groupViewModel.fetchGroupMembers(groupId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {

            // Top app bar with a title and back navigation icon
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    // Title text for the screen
                    Text(
                        "Editar Despesa",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                navigationIcon = {

                    // Back navigation icon button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
            )
        },
    ) { innerPadding ->

        // Main content layout with text fields and components
        Column(
            modifier = Modifier
                .padding(top = 110.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // "Tipo" text label
            Text(
                text = "Tipo",
                modifier = Modifier
                    .padding(end = 200.dp),
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
            )

            // Expense type display (picture or icon)
            ExpenseType()

            EditSplitButton(
                onClick = { showDialogState.value = true },
                navController = navController
            )

            // Split expense dialog
            EditSplitExpenseDialog(
                showDialog = showDialogState,
                groupMembers = groupMembers ?: emptyList(),
                loggedInUser = currentUser!!,
                selectedUsers = selectedUsersState,
                onUsersSelected = { users ->
                    selectedUsersState.value = users
                }
            )


            Column(
                modifier = Modifier
                    .padding(1.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Expense description text field
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(
                            "Descrição",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                TextField(
                    value = location,
                    onValueChange = {
                        location = it
                        expenseViewModel.searchLocations(it)
                        showLazyColumn.value = it.isNotEmpty()
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(
                            "Localização",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                if (showLazyColumn.value) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dynamicHeight)
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        items(autocompleteResults) { place ->
                            Text(
                                text = place.properties.formatted,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        location = place.properties.formatted
                                        showLazyColumn.value = false
                                    }
                            )
                        }
                    }
                }
                TextField(
                    value = amount,
                    onValueChange = { newText ->
                        // Allow only numeric input
                        if (newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newText
                        }
                    },
                    placeholder = {
                        Text(
                            "Valor",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }

            CalendarDialog(
                state = calendarState,
                config = CalendarConfig(
                    monthSelection = true,
                    yearSelection = true
                ),
                selection = CalendarSelection.Date { date ->
                    val newDate = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
                    selectedDate = newDate
                }
            )

            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .border(
                            color = MaterialTheme.colorScheme.onBackground,
                            width = 1.dp,
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 45.dp, vertical = 1.dp)
                    ) {
                        // Display selected date
                        Text(
                            text = if (selectedDate.isNotBlank()) "Data Selecionada: $selectedDate" else "Selecione uma data",
                            modifier = Modifier
                                .weight(1f),
                            textAlign = TextAlign.Center
                        )
                        // Icon to open calendar dialog
                        IconButton(onClick = {
                            calendarState.show()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Selecionar data"
                            )
                        }
                    }
                }
            }

            // File input field for additional information
            FileInputEditExpense(onClick = {})

            Button(
                onClick = {
                    if (description.isBlank() || location.isBlank() || amount.isBlank() || selectedDate.isBlank() || selectedUsersState.value.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Os campos não podem estar vazios!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val userIds = selectedUsersState.value.map { it.userId }
                        currentUser?.let { currentUser ->
                            onEvent(
                                ExpenseEvent.UpdateExpense(
                                    expenseId,
                                    location,
                                    description,
                                    amount,
                                    selectedDate,
                                    groupId,
                                    currentUser.userId,
                                    userIds
                                )
                            )
                        }
                        Toast.makeText(
                            context,
                            "Despesa editada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {

                // Button label text
                Text(
                    "Editar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Composable function for the button to edit participants in the expense.
 *
 * @param onClick The event to be triggered when the button is clicked.
 * @param navController The navigation controller for handling back navigation.
 */
@Composable
fun EditSplitButton(onClick: () -> Unit, navController: NavController) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 10.dp)
            .padding(horizontal = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff301934),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Text(text = "Participantes", color = Color.White)
        }
    }
}

/**
 * Composable function for the button that triggers the camera to take a picture of the receipt.
 *
 * @param onClick The event to be triggered when the button is clicked.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun FileInputEditExpense(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.5.dp)
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
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

/**
 * Composable function for the dialog to edit participants in the expense.
 *
 * @param showDialog The state of the dialog.
 * @param groupMembers The list of group members.
 * @param loggedInUser The logged in user.
 * @param selectedUsers The list of selected users.
 * @param onUsersSelected The event to be triggered when users are selected.
 */
@Composable
fun EditSplitExpenseDialog(
    showDialog: MutableState<Boolean>,
    groupMembers: List<User>,
    loggedInUser: User,
    selectedUsers: MutableState<List<User>>,
    onUsersSelected: (List<User>) -> Unit
) {
    val currentSelectedUsers =
        remember { mutableStateListOf<User>().apply { addAll(selectedUsers.value) } }
    var text by remember { mutableStateOf("") }

    val filteredMembers = remember(groupMembers, loggedInUser) {
        groupMembers.filter { it.userId != loggedInUser.userId }
    }

    if (showDialog.value) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.secondary,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Repartir Com:") },
            text = {
                Column {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Email", color = Color.White) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                    ) {
                        val usersToShow =
                            filteredMembers.filter { it.email.contains(text, ignoreCase = true) }
                        items(usersToShow) { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = currentSelectedUsers.contains(user),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked && !currentSelectedUsers.contains(user)) {
                                            currentSelectedUsers.add(user)
                                        } else if (!isChecked) {
                                            currentSelectedUsers.remove(user)
                                        }
                                    }
                                )
                                Text(
                                    buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Email: ")
                                        }
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(user.email)
                                        }
                                    },
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUsersSelected(currentSelectedUsers)
                        showDialog.value = false
                    }
                ) {
                    Text("Feito", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff8b0000))
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}



