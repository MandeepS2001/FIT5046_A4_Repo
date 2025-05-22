package com.example.food_donation_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import com.example.food_donation_app.components.BottomNavItem
import com.example.food_donation_app.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: User? = null,
    onNavigate: (String) -> Unit = {},
    onSettingClick: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings" + (user?.firstName?.let { " - $it" } ?: ""),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = BottomNavItem.Settings.route,
                onItemClick = { onNavigate(it.route) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Account Section
            item {
                SettingsSectionHeader(title = "Account")
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.AccountCircle,
                    title = "Account Setting",
                    onClick = { onNavigate("account_settings") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    title = "Security",
                    onClick = { onNavigate("security") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    onClick = { onNavigate("notifications") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    title = "Privacy",
                    onClick = { onNavigate("privacy") }
                )
            }

            // Support & About Section
            item {
                SettingsSectionHeader(title = "Support & About")
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Call,
                    title = "Help & Support",
                    onClick = { onNavigate("help_support") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "About",
                    onClick = { onNavigate("about") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Settings,
                    title = "Terms and Policies",
                    onClick = { onNavigate("terms_policies") }
                )
            }

            // Accessibility Section
            item {
                SettingsSectionHeader(title = "Accessibility")
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Visibility,
                    title = "Vision Accessibility",
                    onClick = { onNavigate("vision_accessibility") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.VolumeUp,
                    title = "Audio Accessibility",
                    onClick = { onNavigate("audio_accessibility") }
                )
            }

            // Actions Section
            item {
                SettingsSectionHeader(title = "Actions")
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Warning,
                    title = "Report Problem",
                    onClick = { onNavigate("report_problem") }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.ExitToApp,
                    title = "Logout",
                    onClick = { onLogout() }
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreen
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SettingsApp(user: User? = null, onNavigate: (String) -> Unit = {}, onLogout: () -> Unit = {}) {
    MaterialTheme {
        SettingsScreen(user = user, onNavigate = onNavigate, onLogout = onLogout)
    }
}