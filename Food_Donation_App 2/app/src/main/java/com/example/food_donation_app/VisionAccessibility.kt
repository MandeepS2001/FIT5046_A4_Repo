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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionAccessibilityScreen(
    onBackClick: () -> Unit = {}
) {
    var highContrastEnabled by remember { mutableStateOf(false) }
    var largeTextEnabled by remember { mutableStateOf(false) }
    var boldTextEnabled by remember { mutableStateOf(false) }
    var screenReaderEnabled by remember { mutableStateOf(false) }
    var selectedTextSize by remember { mutableStateOf("Medium") }
    var selectedColorScheme by remember { mutableStateOf("Default") }

    val textSizeOptions = listOf("Small", "Medium", "Large", "Extra Large")
    val colorSchemeOptions = listOf("Default", "High Contrast", "Dark Mode", "Color Blind Friendly")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Vision Accessibility",
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
            // Text Size Section
            AccessibilitySection(
                title = "Text Size",
                icon = Icons.Default.TextFields
            ) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTextSize,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Text Size") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        textSizeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTextSize = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Color Scheme Section
            AccessibilitySection(
                title = "Color Scheme",
                icon = Icons.Default.Palette
            ) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedColorScheme,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Color Scheme") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        colorSchemeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedColorScheme = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Additional Options Section
            AccessibilitySection(
                title = "Additional Options",
                icon = Icons.Default.Settings
            ) {
                AccessibilityToggleItem(
                    icon = Icons.Default.Contrast,
                    title = "High Contrast Mode",
                    description = "Increase contrast for better visibility",
                    checked = highContrastEnabled,
                    onCheckedChange = { highContrastEnabled = it }
                )
                AccessibilityToggleItem(
                    icon = Icons.Default.FormatSize,
                    title = "Large Text",
                    description = "Increase text size throughout the app",
                    checked = largeTextEnabled,
                    onCheckedChange = { largeTextEnabled = it }
                )
                AccessibilityToggleItem(
                    icon = Icons.Default.FormatBold,
                    title = "Bold Text",
                    description = "Make text bold for better readability",
                    checked = boldTextEnabled,
                    onCheckedChange = { boldTextEnabled = it }
                )
                AccessibilityToggleItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Screen Reader",
                    description = "Enable screen reader support",
                    checked = screenReaderEnabled,
                    onCheckedChange = { screenReaderEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preview Section
            AccessibilitySection(
                title = "Preview",
                icon = Icons.Default.Visibility
            ) {
                Text(
                    text = "Sample Text",
                    fontSize = when (selectedTextSize) {
                        "Small" -> 14.sp
                        "Medium" -> 16.sp
                        "Large" -> 18.sp
                        "Extra Large" -> 20.sp
                        else -> 16.sp
                    },
                    fontWeight = if (boldTextEnabled) FontWeight.Bold else FontWeight.Normal,
                    color = when (selectedColorScheme) {
                        "High Contrast" -> Color.Black
                        "Dark Mode" -> Color.White
                        "Color Blind Friendly" -> Color(0xFF2B579A)
                        else -> Color.Black
                    }
                )
            }

            // Save Button
            Button(
                onClick = {
                    // TODO: Save accessibility settings
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text("Save Changes", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun AccessibilitySection(
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
fun AccessibilityToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = PrimaryGreen)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
} 