package com.example.cmu_g10.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
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
import com.example.cmu_g10.Data.User.UserViewModel

/**
 * Composable function representing the "PasswordRecoveryScreen."
 * This screen allows the user to recover their password.
 *
 * @param navController The navigation controller for handling back navigation.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordRecoveryScreen(navController: NavController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

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
                        "Recuperar Palavra-Passe",
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
            val context = LocalContext.current
            Box(
                modifier = Modifier
                    .fillMaxWidth(),

                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        if (isValidEmail(email)) {
                            isLoading = true
                            viewModel.sendPasswordResetEmail(email,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Email enviado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading = false
                                    navController.navigate("login/Register")
                                },
                                onError = { error ->
                                    errorMessage = error
                                    isLoading = false
                                }
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor, insira um email válido.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 315.dp)
                        .padding(30.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Recuperar Password",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator() // Show loading indicator
                }
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage) // Show error message
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Text
            Text(
                "Insira o seu email",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(top = 240.dp)
                    .padding(horizontal = 30.dp),
                textAlign = TextAlign.Start
            )
        }
        // Set up the main content layout with text fields and other components
        Column(
            modifier = Modifier
                .padding(top = 270.dp)
                .padding(horizontal = 12.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Text
            EmailFields(email, onEmailChange = { email = it })
        }
    }
}

/**
 * Composable function for email fields.
 *
 * @param email The email of the user.
 * @param onEmailChange The callback to be invoked when the email changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailFields(email: String, onEmailChange: (String) -> Unit) {

    // Define a list of fields with labels and values
    val fields = listOf(
        "Email" to email,
    )

    Column(
        modifier = Modifier
            .fillMaxHeight(1f),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            singleLine = true,
            label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.small.copy(all = CornerSize(4.dp)),
        )
    }
}

