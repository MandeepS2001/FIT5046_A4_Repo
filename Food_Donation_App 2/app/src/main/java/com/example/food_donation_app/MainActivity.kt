package com.example.food_donation_app

import androidx.compose.ui.Alignment
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.food_donation_app.ui.theme.Food_Donation_AppTheme
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var scheduledDonationRepository: ScheduledDonationRepository

    // Helper function to navigate back to a specific route
    private fun navigateBackTo(navController: NavController, destination: String) {
        navController.popBackStack(destination, inclusive = false)
    }

    // Helper function for settings sub-pages
    private fun navigateBackToSettings(navController: NavController) {
        navigateBackTo(navController, "settings")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize repositories and helpers
        val database = AppDatabase.getDatabase(this)
        val firebaseRepository = FirebaseRepository()
        val hybridRepository = HybridUserRepository(database.userDao(), firebaseRepository)
        val googleSignInHelper = GoogleSignInHelper(this)

        // Initialize donation reminder system
        scheduledDonationRepository = ScheduledDonationRepository(application)
        initializeDonationReminderSystem()

        // Initialize ViewModel
        val viewModelFactory = AuthViewModelFactory(hybridRepository, googleSignInHelper)
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]

        setContent {
            Food_Donation_AppTheme {
                var showSplash by remember { mutableStateOf(true) }
                val loginState by authViewModel.loginState.collectAsState()
                val signupState by authViewModel.signupState.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()
                val navController = rememberNavController()

                if (showSplash) {
                    SplashScreen(onSplashComplete = { showSplash = false })
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = if (currentUser != null) "home" else "auth"
                    ) {
                        composable("home") {
                            HomeScreen(
                                user = currentUser,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("auth") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("find_food_banks") {
                            FindFoodBankScreen(
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        // FIXED: Settings navigation simplified
                        composable("settings") {
                            SettingsScreen(
                                user = currentUser,
                                onNavigate = { route ->
                                    navController.navigate(route)
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("auth") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Account Settings with proper back navigation
                        composable("account_settings") {
                            val updateState by authViewModel.updateState.collectAsState()

                            AccountSettingsScreen(
                                user = currentUser,
                                onBackClick = { navigateBackToSettings(navController) },
                                onSaveChanges = { firstName, lastName, email, phoneNumber, dateOfBirth, gender, street, suburb, state, postalCode ->
                                    authViewModel.updateUser(
                                        firstName, lastName, email, phoneNumber,
                                        dateOfBirth, gender, street, suburb, state, postalCode
                                    )
                                }
                            )

                            // Handle update state
                            LaunchedEffect(updateState) {
                                when (updateState) {
                                    is UpdateState.Success -> {
                                        // Stay on current page, just reset state
                                        authViewModel.resetUpdateState()
                                    }
                                    is UpdateState.Error -> {
                                        // Show error message
                                        authViewModel.resetUpdateState()
                                    }
                                    else -> {}
                                }
                            }
                        }

                        composable("profile") {
                            DonationHistoryScreen(
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        composable("auth") {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    authViewModel.login(email, password)
                                },
                                onGoogleSignInClick = {
                                    authViewModel.signInWithGoogle()
                                },
                                onSignUpClick = { email, password, confirmPassword, firstName, lastName,
                                                  phoneNumber, dateOfBirth, gender, street, suburb, state, postalCode ->
                                    authViewModel.signup(
                                        email, password, confirmPassword, firstName, lastName,
                                        phoneNumber, dateOfBirth, gender, street, suburb, state, postalCode
                                    )
                                },
                                loginState = loginState,
                                signupState = signupState
                            )

                            // Handle login state
                            LaunchedEffect(loginState) {
                                when (loginState) {
                                    is LoginState.Success -> {
                                        navController.navigate("home") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                        authViewModel.resetLoginState()
                                    }
                                    is LoginState.Error -> {
                                        // Error is handled in the UI
                                        authViewModel.resetLoginState()
                                    }
                                    else -> {}
                                }
                            }

                            // Handle signup state
                            LaunchedEffect(signupState) {
                                when (signupState) {
                                    is SignupState.Success -> {
                                        // User is automatically logged in after signup
                                        authViewModel.resetSignupState()
                                    }
                                    is SignupState.Error -> {
                                        // Error is handled in the UI
                                        authViewModel.resetSignupState()
                                    }
                                    else -> {}
                                }
                            }
                        }

                        composable("donation_history") {
                            DonationHistoryScreen()
                        }

                        composable("account") {
                            AccountScreen()
                        }

                        // Security with proper back navigation
                        composable("security") {
                            SecurityScreen(
                                onBackClick = { navigateBackToSettings(navController) },
                                onDeleteAccount = {
                                    authViewModel.logout()
                                    navController.navigate("auth") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Notifications with proper back navigation
                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Report Problem with proper back navigation
                        composable("report_problem") {
                            ReportProblemScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Privacy with proper back navigation
                        composable("privacy") {
                            PrivacyScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Help & Support with proper back navigation
                        composable("help_support") {
                            HelpSupportScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // About with proper back navigation
                        composable("about") {
                            AboutScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Terms & Policies with proper back navigation
                        composable("terms_policies") {
                            TermsAndConditionsScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Vision Accessibility with proper back navigation
                        composable("vision_accessibility") {
                            VisionAccessibilityScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // Audio Accessibility with proper back navigation
                        composable("audio_accessibility") {
                            AudioAccessibilityScreen(
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }

                        // News Screen Route
                        composable("news") {
                            NewsScreen(
                                onBackClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        // Donation Reminder Settings with proper back navigation
                        composable("donation_reminders") {
                            DonationReminderSettingsScreen(
                                scheduledDonationRepository = scheduledDonationRepository,
                                onBackClick = { navigateBackToSettings(navController) }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialize the donation reminder system
     * This sets up WorkManager to run background reminder checks
     */
    private fun initializeDonationReminderSystem() {
        try {
            // Schedule daily reminder checks at 9:00 AM
            scheduledDonationRepository.scheduleDailyReminderChecks()

            // For testing purposes - uncomment the line below to check reminders every 2 hours
            // scheduledDonationRepository.scheduleFrequentReminderChecks()

            android.util.Log.d("MainActivity", "Donation reminder system initialized successfully")

        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Failed to initialize donation reminder system", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optional: Clean up if needed
        // Note: WorkManager will continue running even after app is destroyed
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, user: User?, onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$title" + (user?.firstName?.let { " - $it" } ?: "")) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = title.lowercase().replace(" ", "_"),
                onItemClick = { item -> onNavigate(item.route) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("$title Screen", fontSize = 24.sp)
        }
    }
}

// Optional: Create a settings screen for donation reminders
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationReminderSettingsScreen(
    scheduledDonationRepository: ScheduledDonationRepository,
    onBackClick: () -> Unit
) {
    var testModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donation Reminders") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.Card(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Reminder Settings",
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                    Text("Daily reminder checks are automatically scheduled at 9:00 AM")

                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                    // Test mode toggle
                    androidx.compose.foundation.layout.Row(
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column {
                            Text("Test Mode")
                            Text(
                                text = "Check reminders every 2 hours (for testing)",
                                fontSize = 12.sp,
                                color = androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = testModeEnabled,
                            onCheckedChange = { enabled ->
                                testModeEnabled = enabled
                                if (enabled) {
                                    scheduledDonationRepository.scheduleFrequentReminderChecks()
                                } else {
                                    scheduledDonationRepository.cancelFrequentReminderChecks()
                                    scheduledDonationRepository.scheduleDailyReminderChecks()
                                }
                            }
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                    // Manual trigger button
                    androidx.compose.material3.Button(
                        onClick = {
                            scheduledDonationRepository.scheduleImmediateReminderCheck()
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Trigger Reminder Check Now")
                    }
                }
            }
        }
    }
}