package com.example.food_donation_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit = {}
) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedPrivacy by remember { mutableStateOf(false) }
    var acceptedCookies by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Terms & Conditions",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Terms of Service
            TermsSection(
                title = "Terms of Service",
                icon = Icons.Default.Description
            ) {
                Text(
                    text = "By using the Food Donation App, you agree to these terms. Please read them carefully.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TermsItem(
                    title = "1. Acceptance of Terms",
                    content = "By accessing or using the Food Donation App, you agree to be bound by these Terms of Service."
                )
                TermsItem(
                    title = "2. User Responsibilities",
                    content = "You are responsible for maintaining the confidentiality of your account and for all activities that occur under your account."
                )
                TermsItem(
                    title = "3. Donation Guidelines",
                    content = "All food donations must be safe for consumption and within their expiration date."
                )
                TermsItem(
                    title = "4. Prohibited Activities",
                    content = "Users may not use the app for any illegal purposes or in violation of any local, state, national, or international law."
                )
            }

            // Privacy Policy
            TermsSection(
                title = "Privacy Policy",
                icon = Icons.Default.PrivacyTip
            ) {
                Text(
                    text = "We respect your privacy and are committed to protecting your personal data.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TermsItem(
                    title = "1. Data Collection",
                    content = "We collect information that you provide directly to us, including your name, email address, and location data."
                )
                TermsItem(
                    title = "2. Data Usage",
                    content = "We use your data to provide and improve our services, communicate with you, and ensure the safety of our community."
                )
                TermsItem(
                    title = "3. Data Protection",
                    content = "We implement appropriate security measures to protect your personal information."
                )
            }

            // Cookie Policy
            TermsSection(
                title = "Cookie Policy",
                icon = Icons.Default.Cookie
            ) {
                Text(
                    text = "We use cookies to enhance your experience and analyze our traffic.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TermsItem(
                    title = "1. Types of Cookies",
                    content = "We use essential cookies for app functionality and analytics cookies to improve our services."
                )
                TermsItem(
                    title = "2. Cookie Management",
                    content = "You can control and manage cookies through your device settings."
                )
            }

            // Acceptance Checkboxes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Acceptance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TermsCheckbox(
                        text = "I accept the Terms of Service",
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it }
                    )
                    TermsCheckbox(
                        text = "I accept the Privacy Policy",
                        checked = acceptedPrivacy,
                        onCheckedChange = { acceptedPrivacy = it }
                    )
                    TermsCheckbox(
                        text = "I accept the Cookie Policy",
                        checked = acceptedCookies,
                        onCheckedChange = { acceptedCookies = it }
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    // TODO: Save acceptance status
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                enabled = acceptedTerms && acceptedPrivacy && acceptedCookies
            ) {
                Text("Accept All", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TermsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
            content()
        }
    }
}

@Composable
fun TermsItem(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryGreen
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TermsCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryGreen,
                uncheckedColor = Color.Gray
            )
        )
        Text(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
} 