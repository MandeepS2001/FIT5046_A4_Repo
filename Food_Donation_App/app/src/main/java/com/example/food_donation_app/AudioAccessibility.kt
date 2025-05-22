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
fun AudioAccessibilityScreen(
    onBackClick: () -> Unit = {}
) {
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var voiceOverEnabled by remember { mutableStateOf(false) }
    var selectedVoiceSpeed by remember { mutableStateOf("Normal") }
    var selectedVoiceType by remember { mutableStateOf("Default") }

    val voiceSpeedOptions = listOf("Slow", "Normal", "Fast")
    val voiceTypeOptions = listOf("Default", "Male", "Female", "Child")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Audio Accessibility",
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
            // Sound Settings Section
            AudioAccessibilitySection(
                title = "Sound Settings",
                icon = Icons.Default.VolumeUp
            ) {
                AudioAccessibilityToggleItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Sound Effects",
                    description = "Enable sound effects for app interactions",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
                AudioAccessibilityToggleItem(
                    icon = Icons.Default.Vibration,
                    title = "Vibration",
                    description = "Enable vibration feedback",
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
            }

            // Voice Settings Section
            AudioAccessibilitySection(
                title = "Voice Settings",
                icon = Icons.Default.RecordVoiceOver
            ) {
                AudioAccessibilityToggleItem(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "Voice Over",
                    description = "Enable voice over for screen reading",
                    checked = voiceOverEnabled,
                    onCheckedChange = { voiceOverEnabled = it }
                )

                if (voiceOverEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Speed
                    var expandedSpeed by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedSpeed,
                        onExpandedChange = { expandedSpeed = it }
                    ) {
                        OutlinedTextField(
                            value = selectedVoiceSpeed,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Voice Speed") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpeed) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedSpeed,
                            onDismissRequest = { expandedSpeed = false }
                        ) {
                            voiceSpeedOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedVoiceSpeed = option
                                        expandedSpeed = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Type
                    var expandedType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = it }
                    ) {
                        OutlinedTextField(
                            value = selectedVoiceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Voice Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            voiceTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedVoiceType = option
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Audio Feedback Section
            AudioAccessibilitySection(
                title = "Audio Feedback",
                icon = Icons.Default.Feedback
            ) {
                Text(
                    text = "Test your audio settings",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        // TODO: Play test sound
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    )
                ) {
                    Text("Play Test Sound", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    // TODO: Save audio accessibility settings
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
fun AudioAccessibilitySection(
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
fun AudioAccessibilityToggleItem(
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