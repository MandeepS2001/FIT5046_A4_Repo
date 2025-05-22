package com.example.food_donation_app

// Import necessary Compose UI components for layout and styling
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
// Import Compose runtime annotations and utilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
// Import UI alignment and styling utilities
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import app theme colors
import com.example.food_donation_app.ui.theme.PrimaryGreen
// Import coroutines for delay functionality
import kotlinx.coroutines.delay

/**
 * Splash Screen Composable
 * 
 * This composable displays the app's splash screen that appears when the app starts.
 * It shows the app name and tagline on a green background for a brief period (2 seconds).
 * 
 * @param onSplashComplete A callback function that will be called when the splash screen duration is complete
 */
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    // LaunchedEffect ensures this code runs only once when the composable enters the composition
    // It handles the timing for how long the splash screen is displayed
    LaunchedEffect(key1 = true) {
        // Delay for 2000 milliseconds (2 seconds)
        delay(2000)
        // Call the callback to signal the splash screen is complete
        onSplashComplete()
    }

    // Box layout fills the entire screen with the primary green color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGreen),
        contentAlignment = Alignment.Center
    ) {
        // Column layout to arrange elements vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TODO: You can add an app logo or icon image here if needed

            // App name text
            Text(
                text = "Food Donation",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Space between app name and tagline
            Spacer(modifier = Modifier.height(16.dp))

            // App tagline text
            Text(
                text = "Share Food, Share Hope",
                fontSize = 21.sp,
                color = Color.White
            )
        }
    }
}