package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupState
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Services.Notifications.NotificationsService
import kotlin.reflect.KFunction1

/**
 * Composable function representing the "Adicionar Grupo" screen.
 * Uses the [Scaffold] component for overall layout structure with a top app bar and a bottom "Criar" button.
 * The main content includes text fields for group details and an avatar section with an editable picture and an edit indicator.
 *
 * @param navController The navigation controller for handling back navigation.
 * @param state The current state of the group details.
 * @param onEvent The event handler for managing group-related events.
 * @param userViewModel The view model for the user data.
 * @param groupViewModel The view model for the group data.
 * @param contactEmails The list of emails of the user's contacts.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddGroupScreen(
    navController: NavController,
    state: GroupState,
    onEvent: KFunction1<GroupEvent, Unit>,
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    contactEmails: List<String>
) {
    val context = LocalContext.current
    val selectedUsers = remember { mutableStateListOf<User>() }
    val currentUser by userViewModel.loggedUserData.observeAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Top app bar with title and back navigation icon
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    // Title text for the screen
                    Text(
                        "Adicionar Grupo",
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
        // Bottom bar with a "Criar" button
        bottomBar = {
            // Box to align the button at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        if (state.name.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "O nome do grupo não pode estar vazio!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (
                            selectedUsers.isEmpty()
                        ) {
                            Toast.makeText(
                                context,
                                "O grupo deve ter pelo menos um membro!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Grupo criado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (selectedUsers.isEmpty() && currentUser != null) {
                                if (!selectedUsers.any { it.userId == currentUser!!.userId }) {
                                    selectedUsers.add(currentUser!!)
                                }
                            }
                            onEvent(
                                GroupEvent.SaveGroup(
                                    state.name.value,
                                    selectedUsers
                                )
                            )
                            val notificationsService = NotificationsService(context)
                            val groupName = state.name.value
                            notificationsService.showNotification(
                                title = "Grupo Criado",
                                text = "Seu novo grupo '$groupName' foi criado com sucesso!"
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 200.dp)
                        .padding(32.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Button label text
                    Text(
                        "Criar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        // Main content layout with text fields and components
        Column(
            modifier = Modifier
                .padding(top = 150.dp)
                .padding(horizontal = 20.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Avatar" text label
            Text(
                text = "Avatar",
                modifier = Modifier
                    .padding(end = 200.dp),
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Call GroupPictureWithEditIndicator() and GroupFields() composable functions
            GroupPictureWithEditIndicator()
            GroupFields(
                navController = navController,
                state = state,
                onEvent = onEvent,
                viewModel = userViewModel,
                selectedUsers = selectedUsers,
                contactEmails = contactEmails
            )
        }
    }
}

/**
 * Composable function for the dialog that allows the user to select other users to add to the group.
 *
 * @param showDialog The state of the dialog.
 * @param userList The list of users to display.
 * @param loggedInUser The logged-in user.
 * @param onUsersSelected The event handler for managing user selection.
 */
@Composable
fun UserSelectDialog(
    showDialog: MutableState<Boolean>,
    userList: List<User>,
    loggedInUser: User,
    onUsersSelected: (List<User>) -> Unit
) {
    val selectedUsers = remember { mutableStateListOf<User>().apply { add(loggedInUser) } }
    var text by remember { mutableStateOf("") }

    // Filter out the logged-in user from the display list
    val filteredUserList = remember(userList, loggedInUser) {
        userList.filter { it.userId != loggedInUser.userId }
    }

    if (showDialog.value) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.secondary,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Selecione membros") },
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
                            filteredUserList.filter { it.email.contains(text, ignoreCase = true) }
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
 * Composable function for group fields.
 *
 * @param navController The navigation controller for handling back navigation.
 * @param state The current state of the group details.
 * @param onEvent The event handler for managing group-related events.
 * @param viewModel The view model for the user data.
 * @param selectedUsers The list of selected users.
 * @param contactEmails The list of emails of the user's contacts.
 */
@Composable
fun GroupFields(
    navController: NavController,
    state: GroupState,
    onEvent: (GroupEvent) -> Unit,
    viewModel: UserViewModel,
    selectedUsers: MutableList<User>,
    contactEmails: List<String>

) {
    val showDialogState = remember { mutableStateOf(false) }
    val usersList by viewModel.allUsers.observeAsState()
    val filteredUsersList = usersList?.filter { user ->
        contactEmails.contains(user.email)
    }
    val currentUser by viewModel.loggedUserData.observeAsState()


    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text field for the group name
        TextField(
            value = state.name.value,
            onValueChange = { state.name.value = it },
            placeholder = {
                Text(
                    "Nome do Grupo",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
        )

        AddMemberButton(
            onClick = { showDialogState.value = true },
        )

        currentUser?.let {
            UserSelectDialog(
                showDialog = showDialogState,
                userList = filteredUsersList ?: listOf(),
                loggedInUser = it,
                onUsersSelected = { users ->
                    selectedUsers.clear()
                    selectedUsers.addAll(users)
                }
            )
        }
    }
}

/**
 * Composable function for displaying the group profile picture with an edit indicator.
 */
@Composable
fun GroupPictureWithEditIndicator() {
    // Define the painter for the profile picture.
    val profilePicturePainter = painterResource(id = R.drawable.profile_picture)

    // Create a box to hold the profile picture and the edit icon.
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RectangleShape)
            .clickable { /* Define the action to be taken when the box is clicked. */ }
    ) {

        // Display the profile picture.
        Image(
            painter = profilePicturePainter,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)

        )

        // Position the edit icon overlay within the box.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            EditIconOverlay()
        }
    }
}


