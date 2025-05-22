package com.example.food_donation_app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.ui.theme.PrimaryGreen

sealed class BottomNavItem(
    val title: String,
    val icon: String,  // Using emoji icons for now
    val route: String
) {
    object Home : BottomNavItem("Home", "🏠", "home")
    object FindFoodBanks : BottomNavItem("Find Food Banks", "📍", "find_food_banks")
    object Settings : BottomNavItem("Settings", "⚙️", "settings")
    object Profile : BottomNavItem("Profile", "👤", "profile")
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                BottomNavItem.Home,
                BottomNavItem.FindFoodBanks,
                BottomNavItem.Settings,
                BottomNavItem.Profile
            )

            items.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(56.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Text(
                text = item.icon,
                fontSize = 20.sp
            )
        }
        Text(
            text = item.title,
            color = if (isSelected) PrimaryGreen else Color.Gray,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}