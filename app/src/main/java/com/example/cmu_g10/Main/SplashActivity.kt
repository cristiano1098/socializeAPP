package com.example.cmu_g10.main


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cmu_g10.R
import com.example.cmu_g10.ui.theme.CMU_G10Theme
import kotlinx.coroutines.delay

/**
 * Activity for displaying a splash screen before navigating to the main activity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Remember the dark theme state using a mutableStateOf
            val darkTheme by remember { mutableStateOf(true) }

            // Apply the CMU_G10Theme with the specified dark theme state
            CMU_G10Theme(
                darkTheme = darkTheme
            ) {
                // Display the SplashScreen composable
                SplashScreen()
            }
        }
    }

    /**
     * Composable function representing the splash screen with an animated logo and text.
     */
    @Preview
    @Composable
    private fun SplashScreen() {
        // Create an Animatable for controlling the alpha value
        val alpha = remember {
            Animatable(0f)
        }

        // Trigger the animation using LaunchedEffect
        LaunchedEffect(key1 = true) {
            alpha.animateTo(
                1f,
                animationSpec = tween(1500)
            )
            delay(2000)
            // Start the main activity after a delay
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }

        // Box composable to hold the logo, text, and background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .alpha(alpha.value),
            contentAlignment = Alignment.Center
        ) {

            // Column for vertically arranging the logo and text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
            ) {

                // Image composable for displaying the logo
                Image(
                    painter = painterResource(id = R.drawable.socialize_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 20.dp) // Adjust the bottom padding as needed

                )

                // Text composable for displaying the app name
                Text(
                    text = "Socialize",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            // Column for displaying additional text at the bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Adjust the top padding as needed
            ) {

                // Text composable for displaying a tagline
                Text(
                    text = "Connecting People",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 17.sp,
                )
            }
        }
    }
}

