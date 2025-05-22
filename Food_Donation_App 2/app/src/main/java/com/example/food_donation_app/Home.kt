package com.example.food_donation_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import com.example.food_donation_app.components.BottomNavItem
import com.example.food_donation_app.R
import java.util.*

// Data class for search suggestions
data class SearchSuggestion(
    val title: String,
    val description: String,
    val route: String,
    val icon: ImageVector,
    val keywords: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User? = null,
    onSearchClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    scheduledDonationViewModel: ScheduledDonationViewModel = viewModel()
) {
    val userName = user?.firstName ?: "User"
    val userEmail = user?.email ?: ""
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Define all searchable items
    val searchSuggestions = remember {
        listOf(
            SearchSuggestion(
                title = "Notifications",
                description = "Manage notification preferences",
                route = "notifications",
                icon = Icons.Default.Notifications,
                keywords = listOf("notifications", "alerts", "notify", "reminder", "sound", "vibration")
            ),
            SearchSuggestion(
                title = "Account Settings",
                description = "Update your profile information",
                route = "account_settings",
                icon = Icons.Default.AccountCircle,
                keywords = listOf("account", "profile", "personal", "settings", "info", "details", "edit")
            ),
            SearchSuggestion(
                title = "Security",
                description = "Password and security settings",
                route = "security",
                icon = Icons.Default.Security,
                keywords = listOf("security", "password", "login", "auth", "authentication", "safe", "privacy")
            ),
            SearchSuggestion(
                title = "Privacy",
                description = "Privacy settings and data control",
                route = "privacy",
                icon = Icons.Default.PrivacyTip,
                keywords = listOf("privacy", "data", "personal", "information", "sharing", "permissions")
            ),
            SearchSuggestion(
                title = "Help & Support",
                description = "Get help and support",
                route = "help_support",
                icon = Icons.Default.Help,
                keywords = listOf("help", "support", "assistance", "faq", "contact", "guide", "tutorial")
            ),
            SearchSuggestion(
                title = "About",
                description = "About this app",
                route = "about",
                icon = Icons.Default.Info,
                keywords = listOf("about", "info", "version", "app", "information", "details")
            ),
            SearchSuggestion(
                title = "Report Problem",
                description = "Report issues or bugs",
                route = "report_problem",
                icon = Icons.Default.BugReport,
                keywords = listOf("report", "problem", "bug", "issue", "error", "feedback", "complaint")
            ),
            SearchSuggestion(
                title = "Terms & Policies",
                description = "Terms of service and policies",
                route = "terms_policies",
                icon = Icons.Default.Policy,
                keywords = listOf("terms", "policy", "policies", "legal", "agreement", "conditions", "rules")
            ),
            SearchSuggestion(
                title = "Donation History",
                description = "View your donation records",
                route = "donation_history",
                icon = Icons.Default.History,
                keywords = listOf("history", "donations", "records", "past", "previous", "log", "activity")
            ),
            SearchSuggestion(
                title = "Find Food Banks",
                description = "Locate nearby food banks",
                route = "find_food_banks",
                icon = Icons.Default.LocationOn,
                keywords = listOf("find", "food", "banks", "location", "nearby", "map", "search", "locate")
            ),
            SearchSuggestion(
                title = "News",
                description = "Latest food donation news",
                route = "news",
                icon = Icons.Default.Article,
                keywords = listOf("news", "articles", "updates", "stories", "information", "latest", "blog")
            ),
            SearchSuggestion(
                title = "Vision Accessibility",
                description = "Vision accessibility settings",
                route = "vision_accessibility",
                icon = Icons.Default.Visibility,
                keywords = listOf("vision", "accessibility", "sight", "visual", "blind", "contrast", "font", "size")
            ),
            SearchSuggestion(
                title = "Audio Accessibility",
                description = "Audio accessibility settings",
                route = "audio_accessibility",
                icon = Icons.Default.VolumeUp,
                keywords = listOf("audio", "accessibility", "sound", "hearing", "deaf", "volume", "voice")
            )
        )
    }

    // Filter suggestions based on search query
    val filteredSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            searchSuggestions.filter { suggestion ->
                suggestion.keywords.any { keyword ->
                    keyword.contains(searchQuery, ignoreCase = true)
                } || suggestion.title.contains(searchQuery, ignoreCase = true) ||
                        suggestion.description.contains(searchQuery, ignoreCase = true)
            }.take(5) // Show max 5 suggestions
        }
    }

    // Set current user in ViewModel
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            scheduledDonationViewModel.setCurrentUser(userEmail)
        }
    }

    // Collect UI state and data
    val uiState by scheduledDonationViewModel.uiState.collectAsState()
    val upcomingDonations by scheduledDonationViewModel.upcomingDonations.collectAsState(initial = emptyList())
    val completedCount by scheduledDonationViewModel.completedCount.collectAsState(initial = 0)

    // Show success/error messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            scheduledDonationViewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            scheduledDonationViewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome $userName",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = BottomNavItem.Home.route,
                onItemClick = { onNavigate(it.route) }
            )
        },
        floatingActionButton = {
            if (!isSearchActive) { // Hide FAB when search is active
                FloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    containerColor = PrimaryGreen
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Schedule Donation",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Search Bar with Suggestions
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    // Handle direct search - navigate to first matching suggestion
                    val firstMatch = filteredSuggestions.firstOrNull()
                    firstMatch?.let { suggestion ->
                        onNavigate(suggestion.route)
                        searchQuery = ""
                        isSearchActive = false
                    }
                },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Search settings, pages, features...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PrimaryGreen
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            isSearchActive = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search Suggestions
                if (filteredSuggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredSuggestions) { suggestion ->
                            SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = {
                                    onNavigate(suggestion.route)
                                    searchQuery = ""
                                    isSearchActive = false
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty()) {
                    // No results found
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No results",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Try searching for notifications, settings, or help",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Quick access suggestions when search is active but empty
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "Quick Access",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(searchSuggestions.take(6)) { suggestion ->
                            SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = {
                                    onNavigate(suggestion.route)
                                    searchQuery = ""
                                    isSearchActive = false
                                }
                            )
                        }
                    }
                }
            }

            // Main content - only show when search is not active
            if (!isSearchActive) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Fixed Donation Reminder Section
                    DonationReminderSection(
                        upcomingDonations = upcomingDonations,
                        completedCount = completedCount,
                        onCompleteDonation = { donationId ->
                            scheduledDonationViewModel.completeDonation(donationId)
                        },
                        onScheduleNew = { showScheduleDialog = true }
                    )

                    // Recent Impact Statements Section
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent impact statements",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Information",
                                tint = PrimaryGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Impact Cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item {
                                ImpactCard(
                                    title = "5,000 families helped",
                                    subtitle = "this year",
                                    imageResId = R.drawable.impact_families
                                )
                            }
                            item {
                                ImpactCard(
                                    title = "10,000 meals served",
                                    subtitle = "last month",
                                    imageResId = R.drawable.impact_foodbanks
                                )
                            }
                            item {
                                ImpactCard(
                                    title = "15,000 donations",
                                    subtitle = "this month",
                                    imageResId = R.drawable.impact_volunters
                                )
                            }
                            item {
                                ImpactCard(
                                    title = "20 food banks",
                                    subtitle = "partnered",
                                    imageResId = R.drawable.food
                                )
                            }
                        }
                    }

                    // News Section
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "News",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // News Cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item {
                                NewsCard(
                                    title = "New Mobile Food Bank",
                                    imageResId = R.drawable.impact_families
                                )
                            }
                            item {
                                NewsCard(
                                    title = "Rural Food Bank",
                                    imageResId = R.drawable.transfer
                                )
                            }
                            item {
                                NewsCard(
                                    title = "Community Outreach Program",
                                    imageResId = R.drawable.impact_families
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // See all news button
                        Button(
                            onClick = { onNavigate("news") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "See all news",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "See all",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Schedule Donation Dialog
    if (showScheduleDialog) {
        ScheduleDonationDialog(
            onDismiss = { showScheduleDialog = false },
            onSchedule = { donationType, dateTime, location, items ->
                scheduledDonationViewModel.scheduleDonation(
                    donationType = donationType,
                    scheduledDateTime = dateTime,
                    reminderHoursBefore = 24,
                    foodItems = items,
                    location = location
                )
                showScheduleDialog = false
            }
        )
    }
}

@Composable
fun SearchSuggestionItem(
    suggestion: SearchSuggestion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = suggestion.icon,
                contentDescription = suggestion.title,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suggestion.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = suggestion.description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to ${suggestion.title}",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Keep all existing composables (DonationReminderSection, DonationReminderCard, etc.)
// ... [Rest of the existing composables remain the same] ...

// FIXED: Clean Donation Reminder Section without technical data
@Composable
fun DonationReminderSection(
    upcomingDonations: List<ScheduledDonation>,
    completedCount: Int,
    onCompleteDonation: (Long) -> Unit,
    onScheduleNew: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Donation Reminders",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onScheduleNew) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Schedule New",
                    tint = PrimaryGreen
                )
            }
        }

        // Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Week Streak Card (Clean version)
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "2", // Simple static number for now
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "Week Streak",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Completed Card (Clean version)
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = completedCount.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Clean Streak Text
        Text(
            text = "🔥 2 weeks streak! Keep it up!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Donations
        if (upcomingDonations.isNotEmpty()) {
            Text(
                text = "Upcoming Donations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            upcomingDonations.take(3).forEach { donation ->
                DonationReminderCard(
                    donation = donation,
                    onComplete = { onCompleteDonation(donation.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            // Clean Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No upcoming donations",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Tap + to schedule your first donation",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun DonationReminderCard(
    donation: ScheduledDonation,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                donation.getDaysUntilDonation() == 0 -> Color(0xFFFFEBEE) // Today - light red
                donation.getDaysUntilDonation() == 1 -> Color(0xFFFFF3E0) // Tomorrow - light orange
                else -> Color(0xFFE8F5E9) // Future - light green
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date/Time Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = donation.donationType,
                    fontSize = 14.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = donation.getFormattedScheduledDate(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (donation.location.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = donation.location,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = donation.getStatusText(),
                    fontSize = 14.sp,
                    color = when {
                        donation.getDaysUntilDonation() == 0 -> Color.Red
                        donation.getDaysUntilDonation() == 1 -> Color(0xFFFF9800)
                        else -> PrimaryGreen
                    }
                )
            }

            // Complete Button
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Complete",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDonationDialog(
    onDismiss: () -> Unit,
    onSchedule: (String, Long, String, List<String>) -> Unit
) {
    var donationType by remember { mutableStateOf("Weekly") }
    var location by remember { mutableStateOf("") }
    var foodItems by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var expanded by remember { mutableStateOf(false) }

    val donationTypes = listOf("Weekly", "Monthly", "One-time")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Donation") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Donation Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = donationType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Donation Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        donationTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    donationType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Food Bank Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Food Items
                OutlinedTextField(
                    value = foodItems,
                    onValueChange = { foodItems = it },
                    label = { Text("Food Items (optional)") },
                    placeholder = { Text("e.g., Canned goods, Rice, Pasta") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick schedule buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Schedule for tomorrow at 2 PM
                            val tomorrow = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_MONTH, 1)
                                set(Calendar.HOUR_OF_DAY, 14)
                                set(Calendar.MINUTE, 0)
                            }
                            onSchedule(
                                donationType,
                                tomorrow.timeInMillis,
                                location,
                                foodItems.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Tomorrow 2PM", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            // Schedule for next Saturday at 10 AM
                            val nextSaturday = Calendar.getInstance().apply {
                                while (get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                }
                                set(Calendar.HOUR_OF_DAY, 10)
                                set(Calendar.MINUTE, 0)
                            }
                            onSchedule(
                                donationType,
                                nextSaturday.timeInMillis,
                                location,
                                foodItems.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Sat 10AM", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ImpactCard(
    title: String,
    subtitle: String,
    imageResId: Int
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun NewsCard(
    title: String,
    imageResId: Int
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}