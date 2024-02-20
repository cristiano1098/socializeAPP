package com.example.cmu_g10.Screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R


/**
 * Composable function representing the top app bar for the "Manage Group Members" screen.
 *
 * This function creates a top app bar with a title, back button, and specified colors.
 *
 * @param navController The NavController used for navigating between screens.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param groupId The id of the group.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param contactEmails The list of contact emails.
 */
@Composable
fun ManageGroupMembersScreen(
    navController: NavController,
    groupViewModel: GroupViewModel,
    groupId: Int,
    userViewModel: UserViewModel,
    contactEmails: List<String>
) {
    Scaffold(
        topBar = { ManageGroupMembersScreenTopBar(navController) },
        content = { innerPadding ->
            ManageGroupMembersContent(
                groupId,
                modifier = Modifier.padding(innerPadding),
                userViewModel,
                groupViewModel,
                contactEmails
            )
        }
    )
}

/**
 * Composable function representing the content of the "Manage Group Members" screen.
 *
 * This function creates a column layout with an "Add Member" button and a list of group members.
 *
 * @param navController The NavController used for navigating between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageGroupMembersScreenTopBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        title = {
            Text(
                "Gerir Membros",
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
 * Composable function representing the content of the Manage Group Members screen.
 *
 * This function displays a column layout containing an "Add Member" button, a label, and a list of group members.
 *
 * @param groupId The id of the group.
 * @param modifier The modifier to be applied to the layout.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param contactEmails The list of contact emails.
 */
@Composable
fun ManageGroupMembersContent(
    groupId: Int,
    modifier: Modifier,
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    contactEmails: List<String>
) {
    val showDialogState = remember { mutableStateOf(false) }
    val usersList by userViewModel.allUsers.observeAsState()
    val filteredUsersList = usersList?.filter { user ->
        contactEmails.contains(user.email)
    }
    val currentUser by userViewModel.loggedUserData.observeAsState()
    val groupMembers by groupViewModel.groupMembers.observeAsState(initial = listOf())
    val filteredGroupMembers = groupMembers.filter { it.userId != currentUser?.userId }

    LaunchedEffect(groupId) {
        groupViewModel.fetchGroupMembers(groupId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add Member Button
        AddMemberButton(
            onClick = { showDialogState.value = true },
        )

        currentUser?.let {
            UserAddDialog(
                showDialog = showDialogState,
                userList = filteredUsersList ?: listOf(),
                loggedInUser = it,
                existingMembers = groupMembers,
                groupViewModel = groupViewModel,
                groupId = groupId
            )
        }


        // Label indicating "Membros do grupo"
        Text(
            text = "Membros do grupo",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
        )

        // List of members
        LazyColumn {
            items(filteredGroupMembers) { user ->
                ManageGroupMemberItem(
                    user = user,
                    groupId = groupId,
                    groupViewModel = groupViewModel,
                )
            }
        }
    }
}

/**
 * Composable function representing the dialog for adding members to a group.
 *
 * This function displays a dialog with a text field for searching users and a list of users to select from.
 *
 * @param showDialog The state of the dialog.
 * @param userList The list of users.
 * @param loggedInUser The logged-in user.
 * @param existingMembers The list of existing members.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 * @param groupId The id of the group.
 */
@Composable
fun UserAddDialog(
    showDialog: MutableState<Boolean>,
    userList: List<User>,
    loggedInUser: User,
    existingMembers: List<User>,
    groupViewModel: GroupViewModel,
    groupId: Int
) {
    val selectedUsers = remember { mutableStateListOf<User>() }
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val errorState by groupViewModel.errorState.observeAsState()

    LaunchedEffect(errorState) {
        errorState?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Create a list that excludes existing members and the logged-in user
    val filteredUserList = remember(userList, existingMembers, loggedInUser) {
        userList.filter { user ->
            user.userId != loggedInUser.userId && existingMembers.none { member ->
                member.userId == user.userId
            }
        }
    }

    LaunchedEffect(loggedInUser) {
        if (!selectedUsers.contains(loggedInUser)) {
            selectedUsers.add(loggedInUser)
        }
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
                        label = {
                            Text(
                                "Email",
                                color = Color.White
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary)
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
                                        } else if (user != loggedInUser) {
                                            selectedUsers.remove(user)
                                        }
                                    },
                                    enabled = user != loggedInUser
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
                        groupViewModel.onEvent(GroupEvent.AddMembersToGroup(groupId, selectedUsers))
                        showDialog.value = false
                    }
                )
                {
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
 * Composable function representing a card item for a group member in the Manage Group Members screen.
 *
 * This function displays a card containing the user's photo, name, email, and a delete button.
 *
 * @param user The User object representing the group member.
 * @param groupId The id of the group.
 * @param groupViewModel The [GroupViewModel] that holds the state for the group.
 */
@Composable
fun ManageGroupMemberItem(
    user: User,
    groupId: Int,
    groupViewModel: GroupViewModel,
) {
    val context = LocalContext.current
    var showConfirmationDialog by remember { mutableStateOf(false) }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar remoção") },
            text = { Text("Tem certeza de que deseja remover ${user.name} do grupo?") },
            confirmButton = {
                Button(onClick = {
                    groupViewModel.onEvent(GroupEvent.RemoveUserFromGroup(groupId, user.userId))
                    Toast.makeText(context, "Removeste ${user.name} do grupo", Toast.LENGTH_SHORT)
                        .show()
                    showConfirmationDialog = false
                }) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff8b0000)
                    )
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User photo
            Image(
                painter = painterResource(id = R.drawable.profile_picture),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            // User name and email
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                // Name
                Text(
                    text = user.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Email
                Text(
                    text = user.email,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Delete button
            IconButton(
                onClick = {
                    showConfirmationDialog = true
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

/**
 * Composable function representing the "Add Member" button in the Manage Group Members screen.
 *
 * This function displays a button with an icon and text for adding members to the group.
 *
 * @param onClick The lambda function to handle button click events.
 */
@Composable
fun AddMemberButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
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
            Text(text = "Adicionar Membros", color = Color.White)
        }
    }
}