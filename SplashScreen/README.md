# Food Donation App - Splash Screen Implementation

This directory contains all files related to the Splash Screen implementation of the Food Donation App.

## Files Overview

### Core Splash Screen Implementation
- `SplashScreen.kt` - The main Splash Screen composable that displays the app name and tagline for 2 seconds before transitioning to the main app

### Main Activity and Navigation
- `MainActivity.kt` - Contains the app's entry point and integration of the Splash Screen with the navigation system

### Theme and UI Resources
- `ui/theme/Color.kt` - Defines color constants including PrimaryGreen used in the Splash Screen
- `ui/theme/Theme.kt` - Defines the app's theme styling

### App Icons and Manifest
- `AndroidManifest.xml` - App manifest defining entry points and configuration
- `ic_launcher.xml` - Adaptive icon definition
- `ic_launcher_background.xml` - Icon background drawable
- `ic_launcher_foreground.xml` - Icon foreground drawable

## How the Splash Screen Works

1. When the app launches, MainActivity displays the SplashScreen composable first
2. The SplashScreen shows the app name "Food Donation" and tagline "Share Food, Share Hope" against a green background
3. After a 2-second delay (controlled by LaunchedEffect and delay()), the onSplashComplete callback is triggered
4. This callback sets showSplash to false in MainActivity, which then displays the main app navigation

## Customization Options

The splash screen can be customized by:
- Modifying colors in Color.kt (particularly PrimaryGreen)
- Adding an app logo image (currently commented out in SplashScreen.kt)
- Adjusting the delay duration in the LaunchedEffect block
