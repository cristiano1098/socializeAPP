package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupViewModel

/**
 * Composable function representing the "EditGroupScreen."
 * Displays an editable group screen using the [Scaffold] component for overall layout structure.
 *
 * @param navController The navigation controller for handling back navigation.
 * @param viewModel The [GroupViewModel] that holds the state for the group.
 * @param groupId The id of the group.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditGroupScreen(navController: NavController, viewModel: GroupViewModel, groupId: Int) {
    val groupName = remember { mutableStateOf(viewModel.groupDetails.value?.name) }
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Define the top app bar with title and back navigation icon
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        "Editar Grupo",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
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
        },
        // Include a bottom bar with a "Editar" button
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),

                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        val name = groupName.value
                        if (name.isNullOrEmpty()) {
                            Toast.makeText(
                                context,
                                "Nome do grupo não pode ser vazio!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.onEvent(GroupEvent.UpdateGroupName(groupId, name))
                            navController.popBackStack()
                            Toast.makeText(
                                context,
                                "Grupo editado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .padding(bottom = 240.dp)
                        .padding(20.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
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
    ) { innerPadding ->
        // Set up the main content layout with text fields and other components
        Column(
            modifier = Modifier
                .padding(top = 170.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the "Avatar" text
            Text(
                text = "Avatar",
                modifier = Modifier
                    .padding(end = 200.dp),
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.background
            )

            // Call GroupType() and GroupFields() composable functions
            GroupPictureWithEditIndicator()
            EditGroupFields(groupName)
        }
    }
}

/**
 * Composable function for group fields.
 *
 * @param groupName The name of the group.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupFields(groupName: MutableState<String?>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        groupName.value?.let {
            TextField(
                value = it,
                onValueChange = { groupName.value = it },
                label = { Text("Group Name") },
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

