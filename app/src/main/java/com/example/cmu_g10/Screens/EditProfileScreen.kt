package com.example.cmu_g10.Screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.cmu_g10.Data.User.UserEvent
import com.example.cmu_g10.Data.User.UserState
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import java.security.MessageDigest

/**
 * EditProfile Screen
 *
 * This screen provides the user interface for editing a user's profile. It consists of a top app bar for navigation,
 * a profile picture, and editable text fields for user information. The screen layout is built using the Jetpack Compose
 * framework, providing a modern and declarative way to create UI components in Android.
 */

/**
 * Composable function to set up the EditProfile screen with a Scaffold layout.
 * This function creates the overall screen structure including the top bar and content layout.
 *
 * @param navController The NavController for handling navigation events.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 * @param onEvent The lambda function to be called when an event is triggered.
 * @param state The current state of the user.
 */
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    onEvent: (UserEvent) -> Unit,
    state: UserState
) {
    // Scaffold layout definition.
    Scaffold(
        topBar = { EditProfileTopBar(navController) },
        content = { innerPadding ->
            ProfileContent(navController, Modifier.padding(innerPadding), viewModel, onEvent, state)
        }
    )
}

/**
 * Composable function to display the top app bar on the EditProfile screen.
 * It includes a title "Editar Perfil" and a back arrow for navigation.
 *
 * @param navController The NavController to handle the back navigation action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(navController: NavHostController) {
    // TopAppBar with title and navigation icon.
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        title = {
            Text(
                "Editar perfil",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },

        )
}

/**
 * Composable function to lay out the content of the EditProfile screen.
 * It includes a profile picture and editable fields for user information.
 *
 * @param navController The NavController for handling navigation events.
 * @param modifier A Modifier to apply to the Column layout.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 * @param onEvent The lambda function to be called when an event is triggered.
 * @param state The current state of the user.
 */
@Composable
fun ProfileContent(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: UserViewModel,
    onEvent: (UserEvent) -> Unit,
    state: UserState
) {
    // Column layout for profile picture and fields.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ProfilePictureWithEditIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        EditProfile(navController, viewModel)
    }
}

/**
 * Composable function to display the editable fields for user information.
 * This function creates a text field for each piece of information, including name, email, phone number, and password.
 * It also includes a button to submit the changes.
 *
 * @param navController The NavController for handling navigation events.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun EditProfile(
    navController: NavController,
    viewModel: UserViewModel,
) {
    val context = LocalContext.current
    val user by viewModel.loggedUserData.observeAsState()

    // Define the fields for user information.
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(user?.phone ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    // List of fields
    val fields = listOf(
        "Nome" to name,
        "Numero de Telefone" to phoneNumber,
        "Email" to email,
        "Password" to password,
        "Confirmar Password" to confirmPassword
    )

    // Create text fields for each piece of information
    fields.forEach { (label, value) ->
        ProfileTextField(label, value, onValueChange = { newValue ->
            when (label) {
                "Nome" -> name = newValue
                "Email" -> email = newValue
                "Numero de Telefone" -> phoneNumber = newValue
                "Password" -> password = newValue
                "Confirmar Password" -> confirmPassword = newValue
            }
        })
    }

    // Button to submit the changes
    EditButton(onClick = {
        if (password.isNotEmpty() || confirmPassword.isNotEmpty()) {
            // Validate password length
            if (password.length >= 6) {
                // Validate other inputs and check if passwords match
                if (isValid(name, phoneNumber, email, context) && password == confirmPassword) {
                    viewModel.onEvent(
                        UserEvent.UpdateUser(
                            id = user?.userId ?: 0,
                            name = name,
                            email = email,
                            password = password,
                            phone = phoneNumber,
                        )
                    )
                    Toast.makeText(context, "Dados atualizados com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    navController.popBackStack()
                } else {
                    // Show error message for non-matching passwords
                    Toast.makeText(context, "Passwords não são iguais", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show error message for short password
                Toast.makeText(
                    context,
                    "Password deve ter mais de 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // If no new passwords are provided, validate other inputs and update
            if (isValid(name, phoneNumber, email, context)) {
                viewModel.onEvent(
                    UserEvent.UpdateUser(
                        id = user?.userId ?: 0,
                        name = name,
                        email = email,
                        password = null,
                        phone = phoneNumber,
                    )
                )
                Toast.makeText(context, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    })
}

/**
 * Composable function to validate user input.
 * This function checks if the user has provided valid input for each field.
 *
 * @param name The user's name.
 * @param phone The user's phone number.
 * @param email The user's email address.
 * @param context The context of the application.
 */
fun isValid(name: String, phone: String, email: String, context: Context): Boolean {
    // Check if the user has provided a name
    if (name.isBlank()) {
        Toast.makeText(context, "Nome não pode estar vazio", Toast.LENGTH_SHORT).show()
        return false
    }

    // Check if the user has provided a phone number
    if (phone.length > 9 || phone.length < 9) {
        Toast.makeText(context, "Numero inválido", Toast.LENGTH_SHORT).show()
        return false
    }

    // Check if the user has provided an email
    if (!email.contains("@") || !email.contains(".")) {
        Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}


/**
 * Composable function to create a single text field for the user's profile information.
 * The function is reusable for different types of information such as name, email, and password.
 *
 * @param label The label of the text field.
 * @param value The current value of the text field.
 * @param onValueChange The lambda function to be called when the text changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    // OutlinedTextField definition.
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label, color = MaterialTheme.colorScheme.onBackground) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(4.dp)),
        visualTransformation = if (label.contains("Password")) PasswordVisualTransformation() else VisualTransformation.None,
    )
}

/**
 * Composable function to create an Edit button.
 * This button can be clicked to save changes to the user's profile.
 *
 * @param onClick The lambda function to be called when the button is clicked.
 */
@Composable
fun EditButton(onClick: () -> Unit) {
    // Button to submit changes.
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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

/**
 * Composable function that displays a profile picture with an edit icon overlay.
 * The function creates an interactive element where users can click on the picture to potentially edit it.
 * The picture is displayed within a box, and the edit icon appears in the bottom-end corner of this box.
 *
 * @param None Parameters are not used in this function.
 */
@Composable
fun ProfilePictureWithEditIndicator() {
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

/**
 * Composable function to display an edit icon.
 * This function positions a small, circular edit icon with a semi-transparent background.
 *
 * @param None Parameters are not used in this function.
 */
@Composable
fun EditIconOverlay() {
    // Display the edit icon with improved visibility.
    Icon(
        imageVector = Icons.Filled.Edit,
        contentDescription = "Editar",
        tint = Color.White,
        modifier = Modifier
            .size(24.dp)
            .background(
                color = Color.Gray.copy(alpha = 0.9f), // Adjust alpha for better visibility
                shape = CircleShape
            )
            .padding(4.dp)
    )
}

