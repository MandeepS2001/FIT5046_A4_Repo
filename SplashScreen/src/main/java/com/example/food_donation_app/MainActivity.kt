package com.example.food_donation_app

import androidx.compose.ui.Alignment
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.food_donation_app.ui.theme.Food_Donation_AppTheme
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import androidx.compose.material3.TopAppBar
import com.example.food_donation_app.HybridUserRepository
import com.example.food_donation_app.FirebaseRepository
import com.example.food_donation_app.GoogleSignInHelper
import com.example.food_donation_app.AuthViewModel
import com.example.food_donation_app.AuthViewModelFactory
import com.example.food_donation_app.LoginState
import com.example.food_donation_app.SignupState
import com.example.food_donation_app.UpdateState

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize repositories and helpers
        val database = AppDatabase.getDatabase(this)
        val firebaseRepository = FirebaseRepository()
        val hybridRepository = HybridUserRepository(database.userDao(), firebaseRepository)
        val googleSignInHelper = GoogleSignInHelper(this)

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
                        composable("settings") {
                            SettingsScreen(
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
                        composable("account_settings") {
                            val updateState by authViewModel.updateState.collectAsState()

                            AccountSettingsScreen(
                                user = currentUser,
                                onBackClick = {
                                    navController.popBackStack()
                                },
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
                                        // Show success message and navigate back
                                        navController.popBackStack()
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

                        composable("security") {
                            SecurityScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onDeleteAccount = {
                                    authViewModel.logout()
                                    navController.navigate("auth") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("report_problem") {
                            ReportProblemScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("privacy") {
                            PrivacyScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("help_support") {
                            HelpSupportScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("about") {
                            AboutScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("terms_policies") {
                            TermsAndConditionsScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("vision_accessibility") {
                            VisionAccessibilityScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("audio_accessibility") {
                            AudioAccessibilityScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
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