package com.example.cmu_g10.Screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.cmu_g10.Data.User.UserEvent
import com.example.cmu_g10.Data.User.UserState
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Composable function for the screen containing sign-in and registration content.
 *
 * @param navController Navigation controller for handling navigation actions.
 * @param state The state of the input fields.
 * @param onEvent Callback for the various events performed on the input fields.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun LoginRegisterScreen(
    navController: NavHostController,
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    viewModel: UserViewModel,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LoginRegisterContent(navController, state, onEvent, viewModel)
    }
}

/**
 * Composable function for the content of the sign-in and registration screen.
 *
 * @param navController Navigation controller for handling navigation actions.
 * @param state The state of the input fields.
 * @param onEvent Callback for the various events performed on the input fields.
 * @param userViewModel The [UserViewModel] that holds the state for the user.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginRegisterContent(
    navController: NavHostController,
    state: UserState,
    onEvent: (UserEvent) -> Unit,
    userViewModel: UserViewModel,
) {

    var selectedTabIndex by remember { mutableStateOf(0) }

    //Allow notifications permission
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState : PermissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )

                LaunchedEffect(key1 = Unit) {
                    if(!permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Logo()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TabRow(
                containerColor = MaterialTheme.colorScheme.background,
                selectedTabIndex = selectedTabIndex,
                divider = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .padding(horizontal = 30.dp)
                            .padding(top = 17.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    modifier = Modifier.padding(vertical = 7.dp)
                ) {
                    Text(
                        text = "Iniciar Sessão",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    modifier = Modifier.padding(vertical = 7.dp)
                ) {
                    Text(
                        text = "Registar",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // Content below the tabs
            when (selectedTabIndex) {
                0 -> LoginContent(navController, state, userViewModel)
                1 -> RegisterContent(navController, state, userViewModel)
            }

            // Center and place SocialMediaLoginOptions() below the container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SocialMediaLoginOptions()
            }
        }
    }
}

/**
 * Composable function for the login content.
 *
 * @param navController Navigation controller for handling navigation actions.
 * @param state The state of the input fields.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun LoginContent(
    navController: NavHostController,
    state: UserState,
    viewModel: UserViewModel
) {
    val context = LocalContext.current
    var loginAttempted by remember { mutableStateOf(false) }
    val user by viewModel.loggedUserData.observeAsState()
    val loginStatus by viewModel.loginStatus.observeAsState()

    LaunchedEffect(loginStatus) {
        when (loginStatus) {
            UserViewModel.LoginStatus.Success -> {
                Toast.makeText(context, "Login bem sucedido!", Toast.LENGTH_SHORT).show()
                navController.navigate("HomeScreen")
                viewModel.resetLoginStatus()
            }

            UserViewModel.LoginStatus.Failed -> {
                Toast.makeText(context, "Erro no login!", Toast.LENGTH_SHORT).show()
                viewModel.resetLoginStatus()
            }

            else -> {
                viewModel.resetLoginStatus()
            }
        }
    }

    TextField(
        value = state.email.value,
        onValueChange = { state.email.value = it },
        placeholder = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    // Password TextField
    TextField(
        value = state.password.value,
        onValueChange = { state.password.value = it },
        placeholder = { Text("Palavra-Passe", color = MaterialTheme.colorScheme.onBackground) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    Spacer(modifier = Modifier.height(5.dp))

    Text(
        text = "Esqueceste a palavra-passe?",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.clickable { navController.navigate("RecoverPassword") }
    )

    Spacer(modifier = Modifier.height(10.dp))

    LoginButton {
        viewModel.onEvent(
            UserEvent.LoginUser(
                state.email.value,
                state.password.value
            )
        )
    }
}

/**
 * Composable function for the registration content.
 *
 * @param navController Navigation controller for handling navigation actions.
 * @param state The state of the input fields.
 * @param viewModel The [UserViewModel] that holds the state for the user.
 */
@Composable
fun RegisterContent(
    navController: NavController,
    state: UserState,
    viewModel: UserViewModel
) {
    val context = LocalContext.current
    val registrationResult by viewModel.registrationResult.observeAsState()

    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            UserViewModel.RegistrationResult.Success -> {
                Toast.makeText(context, "Registo bem sucedido!", Toast.LENGTH_SHORT).show()
                navController.navigate("HomeScreen")
                viewModel.resetRegistrationResult()
            }

            UserViewModel.RegistrationResult.EmailAlreadyRegistered -> {
                Toast.makeText(context, "Email já registado!", Toast.LENGTH_SHORT).show()
                viewModel.resetRegistrationResult()
            }

            null -> {}
        }
    }

    TextField(
        value = state.name.value,
        onValueChange = { state.name.value = it },
        placeholder = { Text("Nome", color = MaterialTheme.colorScheme.onBackground) },
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    TextField(
        value = state.email.value,
        onValueChange = { state.email.value = it },
        placeholder = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    // Password TextField
    TextField(
        value = state.password.value,
        onValueChange = { state.password.value = it },
        placeholder = { Text("Palavra-Passe", color = MaterialTheme.colorScheme.onBackground) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        // You might want to add VisualTransformation for hiding password
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    // Phone TextField
    TextField(
        value = state.phone.value,
        onValueChange = {
            state.phone.value = it
        },
        placeholder = {
            Text(
                "Número de Telefone",
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Phone
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    RegisterButton {
        if (state.password.value.length < 6) {
            Toast.makeText(context, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (isValidInput(state, context)) {
                viewModel.onEvent(
                    UserEvent.RegisterUser(
                        state.name.value,
                        state.email.value,
                        state.password.value,
                        state.phone.value,
                    )
                )
            }
        }
    }
}

/**
 * Function to check if the input fields are valid.
 *
 * @param state The state of the input fields.
 * @param context The context of the application.
 * @return True if the input fields are valid, false otherwise.
 */
fun isValidInput(state: UserState, context: Context): Boolean {
    // Validate name - example: should not be empty
    if (state.name.value.isBlank()) {
        Toast.makeText(context, "Por favor, insira um nome válido.", Toast.LENGTH_SHORT).show()
        return false
    }

    // Validate email - example: should contain "@" and "."
    if (!state.email.value.contains("@") || !state.email.value.contains(".")) {
        Toast.makeText(context, "Por favor, insira um email válido.", Toast.LENGTH_SHORT).show()
        return false
    }

    // Validate phone - example: should be a certain length
    if (state.phone.value.length != 9) {
        Toast.makeText(
            context,
            "Por favor, insira um número de telefone válido.",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    // All validations passed
    return true
}

/**
 * Composable function for displaying the logo and title of the app.
 */
@Composable
fun Logo() {
    // The logo for the app.
    Image(
        painter = painterResource(id = R.drawable.socialize_logo),
        contentDescription = null,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .size(150.dp)

    )
}

/**
 * Composable function for social media login options.
 */
@Composable
fun SocialMediaLoginOptions() {
    Row(horizontalArrangement = Arrangement.spacedBy(100.dp)) {
        SocialMediaIcon(R.drawable.ic_google, "Login with Google") { /*TODO: Handle Google login*/ }
        SocialMediaIcon(
            R.drawable.ic_facebook,
            "Login with Facebook"
        ) { /*TODO: Handle Facebook login*/ }
    }
}

/**
 * Composable function for social media icons.
 *
 * @param drawableId Resource ID of the drawable icon.
 * @param contentDescription Description for the icon content.
 * @param onClick Callback for the click action on the icon.
 */
@Composable
fun SocialMediaIcon(drawableId: Int, contentDescription: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
    )
}

/**
 * Composable function for the login button.
 *
 * @param onClick Callback for the click action on the button.
 */
@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            "Login",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * Composable function for the registration button.
 *
 * @param onClick Callback for the click action on the button.
 */
@Composable
fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            "Registar",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
