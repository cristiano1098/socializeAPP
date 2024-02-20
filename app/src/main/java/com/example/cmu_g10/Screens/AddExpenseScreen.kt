package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.cmu_g10.Services.Camara.CamaraActivity
import com.example.cmu_g10.Services.AutoComplete.AutocompleteResponse
import com.example.cmu_g10.Services.AutoComplete.GeoapifyApiService
import com.example.cmu_g10.Data.Expense.ExpenseEvent
import com.example.cmu_g10.Data.Expense.ExpenseState
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Services.Notifications.NotificationsService
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import com.example.cmu_g10.main.MainActivity
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Composable function representing the add expense screen.
 *
 * @param groupId The id of the group to which the expense will be added.
 * @param navController The navigation controller used to navigate between composables.
 * @param state The state of the expense.
 * @param onEvent The event to be triggered when an action is performed.
 * @param userViewModel The view model used to manage user data.
 * @param expenseViewModel The view model used to manage expense data.
 * @param groupViewModel The view model used to manage group data.
 * @param contextMain The context of the main activity.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddExpenseScreen(
    groupId: Int,
    navController: NavController,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit,
    userViewModel: UserViewModel,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel,
    contextMain: Context
) {
    val selectedUsers = remember { mutableStateListOf<User>() }
    val showDialogState = remember { mutableStateOf(false) }
    val usersList by userViewModel.allUsers.observeAsState()
    val currentUser by userViewModel.loggedUserData.observeAsState()
    val groupMembers by groupViewModel.groupMembers.observeAsState()
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf<String?>(null) }

    val photoUri by expenseViewModel.photoUri.observeAsState()


    LaunchedEffect(groupId) {
        groupViewModel.fetchGroupMembers(groupId)
    }

    val group by groupViewModel.groupDetails.observeAsState()

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
                        "Adicionar Despesa",
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

            AddSplitButton(
                onClick = { showDialogState.value = true },
                navController = navController
            )

            // Split expense dialog
            currentUser?.let {
                SplitExpenseDialog(
                    showDialog = showDialogState,
                    groupMembers = groupMembers ?: emptyList(),
                    loggedInUser = it,
                    onUsersSelected = { users ->
                        selectedUsers.clear()
                        selectedUsers.addAll(users)
                    }
                )
            }

            // Expense type display (picture or icon)
            ExpenseFields(
                navController,
                state,
                onEvent,
                expenseViewModel,
            )

            DatePickerCol(selectedDate)

            // File input field for additional information
            FileInputEditExpense(onClick = {
                val intent = Intent(context, CamaraActivity::class.java)
                context.startActivity(intent)
            })

            Button(
                onClick = {
                    if (state.description.value.isEmpty() || state.location.value.isEmpty() || state.amount.value.isEmpty() || selectedDate.value == null || selectedUsers.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Os campos não podem estar vazios!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Despesa criada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        currentUser?.let {
                            val userIdsState =
                                mutableStateOf(selectedUsers.map { user -> user.userId })
                            onEvent(
                                ExpenseEvent.AddExpense(
                                    state.description.value,
                                    state.location.value,
                                    state.amount.value,
                                    selectedDate,
                                    groupId,
                                    it.userId,
                                    photoUri ?: "",
                                    userIdsState
                                )
                            )
                        }
                        //Sends a added expense notification
                        val groupName = group?.name ?: "o grupo"
                        val notificationsService = NotificationsService(context)
                        notificationsService.showNotification(
                            title = "Despesa Adicionada",
                            text = "Uma despesa foi adicionada ao grupo $groupName."
                        )
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
                    "Adicionar",
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
 * Composable function for the button to add participants to a expense.
 *
 * @param onClick The action to be performed when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
fun AddSplitButton(onClick: () -> Unit, navController: NavController) {
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
 * Composable function for the dialog to select participants to a expense.
 *
 * @param showDialog The state of the dialog.
 * @param groupMembers The list of group members.
 * @param loggedInUser The logged-in user.
 * @param onUsersSelected The action to be performed when users are selected.
 */
@Composable
fun SplitExpenseDialog(
    showDialog: MutableState<Boolean>,
    groupMembers: List<User>,
    loggedInUser: User,
    onUsersSelected: (List<User>) -> Unit
) {
    val selectedUsers = remember { mutableStateListOf<User>() }
    var text by remember { mutableStateOf("") }

    // Filter out the logged-in user from the group members list
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
                                    checked = selectedUsers.contains(user),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedUsers.add(user)
                                        } else {
                                            selectedUsers.remove(user)
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
                        onUsersSelected(selectedUsers)
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

/**
 * Composable function to dynamically calculate the height of the lazy column. (Autocomplete)
 *
 * @param itemsCount The number of items in the lazy column.
 * @param maxHeightDp The maximum height of the lazy column.
 * @param itemHeightDp The height of each item in the lazy column.
 * @return The height of the lazy column.
 */
@Composable
fun dynamicLazyColumnHeight(itemsCount: Int, maxHeightDp: Dp, itemHeightDp: Dp): Dp {
    val totalHeightDp = itemHeightDp * itemsCount
    return if (totalHeightDp > maxHeightDp) maxHeightDp else totalHeightDp
}

/**
 * Composable function to manage editable text fields for expense details.
 *
 * @param navController The navigation controller used to navigate between composables.
 * @param state The state of the expense.
 * @param onEvent The event to be triggered when an action is performed.
 * @param viewModel The view model used to manage expense data.
 */
@Composable
fun ExpenseFields(
    navController: NavController,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit,
    viewModel: ExpenseViewModel,
) {
    val autocompleteResults by viewModel.autocompleteResults.observeAsState(initial = emptyList())

    val locationQuery = remember { mutableStateOf("") }

    val showLazyColumn = remember { mutableStateOf(true) }

    val maxHeight = 300.dp
    val itemHeight = 45.dp // Approximate height of each item
    val dynamicHeight = dynamicLazyColumnHeight(autocompleteResults.size, maxHeight, itemHeight)

    Column(
        modifier = Modifier
            .padding(1.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Expense description text field
        TextField(
            value = state.description.value,
            onValueChange = {
                state.description.value = it
            },
            placeholder = { Text("Descrição", color = MaterialTheme.colorScheme.onBackground) },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        TextField(
            value = locationQuery.value,
            onValueChange = {
                locationQuery.value = it
                viewModel.searchLocations(it)
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
                                locationQuery.value = place.properties.formatted
                                state.location.value = place.properties.formatted
                                showLazyColumn.value = false
                            }
                    )
                }
            }
        }
        // Expense amount text field
        TextField(
            value = state.amount.value,
            onValueChange = { newText ->
                if (newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                    state.amount.value = newText
                }
            },
            placeholder = { Text("Valor", color = MaterialTheme.colorScheme.onBackground) },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    }
}

/**
 * Composable function to display the expense type (placeholder for a picture).
 */
@Composable
fun ExpenseType() {
    // Load the placeholder picture or icon
    val expensePicturePainter = painterResource(id = R.drawable.restaurant_icon)

    // Create a box to hold the picture or icon with a clickable behavior
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { /* Define the action to be taken when the box is clicked. */ }
    ) {

        // Display the expense picture or icon with content scale set to crop
        Image(
            painter = expensePicturePainter,
            contentDescription = "Expense Type",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)

        )
    }
}

/**
 * Composable function to manage date picker.
 *
 * @param selectedDate The selected date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerCol(selectedDate: MutableState<String?>) {
    val calendarState = rememberUseCaseState()

    // Calendar to select a date to a new expense
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            selectedDate.value = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
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
                //Selected date
                Text(
                    text = selectedDate.value?.let { "Data Selecionada: $it" }
                        ?: "Selecione uma data",
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Center
                )
                //Icon to open calendar dialog
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
}


