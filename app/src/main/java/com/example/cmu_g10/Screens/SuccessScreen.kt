package com.example.cmu_g10.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cmu_g10.R

/**
 * Composable function representing the Success Screen of the Math Quiz app.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param name The username to display in the success message.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(navController: NavHostController, name: String) {

    Scaffold(
        content = { innerPadding ->
            SuccessContent(
                userName = name,
                modifier = Modifier.padding(innerPadding),
                navController
            )
        }
    )
}

/**
 * Composable function representing the content of the Success Screen.
 *
 * @param userName The username to display in the success message.
 * @param modifier The modifier for layout customization.
 * @param navController The navigation controller for navigating between screens.
 */
@Composable
fun SuccessContent(userName: String, modifier: Modifier, navController: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SuccessIcon()

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Sucesso!!",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "A tua conta com o $userName foi acertada",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(70.dp))

        DoneButton { navController.navigate("homeScreen") }
    }
}

/**
 * Composable function representing the "Done" button.
 *
 * @param onClick The callback for the button click event.
 */
@Composable
fun DoneButton(onClick: () -> Unit) {
    // Button to submit changes.
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Feito",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * Composable function representing the success icon.
 */
@Composable
fun SuccessIcon() {
    val successIconPainter = painterResource(id = R.drawable.success_icon)

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RectangleShape)
            .clickable { /* Defina a ação a ser tomada quando a caixa for clicada. */ }
    ) {
        Image(
            painter = successIconPainter,
            contentDescription = "Success Icon",
            contentScale = ContentScale.Crop
        )
    }
}


