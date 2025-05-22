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
fun AboutScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo and Version
            Icon(
                Icons.Default.Favorite,
                contentDescription = "App Logo",
                modifier = Modifier.size(80.dp),
                tint = PrimaryGreen
            )
            
            Text(
                text = "Food Donation App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            
            Text(
                text = "Version 1.0.0",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Description
            AboutSection(
                title = "About the App",
                icon = Icons.Default.Info
            ) {
                Text(
                    text = "The Food Donation App connects food donors with local food banks, making it easy to donate surplus food and help those in need. Our mission is to reduce food waste and fight hunger in our community.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Features
            AboutSection(
                title = "Key Features",
                icon = Icons.Default.Star
            ) {
                FeatureItem(
                    icon = Icons.Default.LocationOn,
                    title = "Find Food Banks",
                    description = "Locate nearby food banks and donation centers"
                )
                FeatureItem(
                    icon = Icons.Default.History,
                    title = "Track Donations",
                    description = "Keep track of your donation history and impact"
                )
                FeatureItem(
                    icon = Icons.Default.Notifications,
                    title = "Real-time Updates",
                    description = "Get notified about urgent food needs"
                )
            }

            // Team
            AboutSection(
                title = "Our Team",
                icon = Icons.Default.Group
            ) {
                TeamMember(
                    name = "John Doe",
                    role = "Founder & CEO",
                    description = "Passionate about reducing food waste and helping communities"
                )
                TeamMember(
                    name = "Jane Smith",
                    role = "Head of Operations",
                    description = "Expert in food distribution and logistics"
                )
                TeamMember(
                    name = "Mike Johnson",
                    role = "Lead Developer",
                    description = "Dedicated to creating impactful technology solutions"
                )
            }

            // Contact
            AboutSection(
                title = "Contact Us",
                icon = Icons.Default.ContactMail
            ) {
                ContactItem(
                    icon = Icons.Default.Email,
                    text = "contact@fooddonationapp.com"
                )
                ContactItem(
                    icon = Icons.Default.Phone,
                    text = "+1 (800) 123-4567"
                )
                ContactItem(
                    icon = Icons.Default.LocationOn,
                    text = "123 Food Street, City, Country"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Media Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialMediaButton(
                    icon = Icons.Default.Share,
                    onClick = { /* TODO: Share app */ }
                )
                SocialMediaButton(
                    icon = Icons.Default.Star,
                    onClick = { /* TODO: Rate app */ }
                )
                SocialMediaButton(
                    icon = Icons.Default.Feedback,
                    onClick = { /* TODO: Send feedback */ }
                )
            }
        }
    }
}

@Composable
fun AboutSection(
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
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = PrimaryGreen)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TeamMember(
    name: String,
    role: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = role,
            fontSize = 14.sp,
            color = PrimaryGreen
        )
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = PrimaryGreen)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SocialMediaButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            icon,
            contentDescription = "Social Media",
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
    }
} 