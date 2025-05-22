//Updated Donation History
package com.example.food_donation_app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import com.example.food_donation_app.components.BottomNavItem
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Paint
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationHistoryScreen(
    donationViewModel: DonationViewModel = viewModel(),
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // Collect data from the Room database via ViewModel
    val donations by donationViewModel.allDonations.collectAsState(initial = emptyList())
    val donationCount by donationViewModel.donationCount.collectAsState(initial = 0)
    val totalWeight by donationViewModel.totalWeight.collectAsState(initial = 0f)
    val locationCount by donationViewModel.locationCount.collectAsState(initial = 0)

    // State for dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDonation by remember { mutableStateOf<Donation?>(null) }

    // For demonstration purposes - add sample data if empty
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (donationCount == 0) {
            donationViewModel.addSampleData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Donation History",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                actions = {
                    // Add new donation button
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Donation",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = BottomNavItem.Profile.route,
                onItemClick = { onNavigate(it.route) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Summary statistics
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Number of Donations
                    SummaryCard(
                        title = donationCount.toString(),
                        subtitle = "Donations",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )

                    // Total Weight
                    SummaryCard(
                        title = String.format("%.2f kg", totalWeight ?: 0f),
                        subtitle = "Total Weight",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Balance,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )

                    // Number of Locations
                    SummaryCard(
                        title = locationCount.toString(),
                        subtitle = "Locations",
                        icon = {

                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }
            }

            // Donation Over Time chart
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Donations Over Time",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        // Line chart implementation
                        DonationsLineChart(
                            modifier = Modifier.fillMaxSize(),
                            data = listOf(2, 6, 8, 7, 10, 8, 6, 7, 9, 14, 8)
                        )
                    }
                }
            }

            // Donation history entries
            items(donations.size) { index ->
                val donation = donations[index]
                DonationHistoryItem(
                    donation = donation,
                    onShowDetails = { /* Implementation for showing details */ },
                    onShare = { /* Implementation for sharing */ },
                    onEdit = {
                        selectedDonation = donation
                        showAddDialog = true
                    },
                    onDelete = {
                        donationViewModel.deleteDonation(donation)
                    }
                )
            }
        }

        // Add/Edit Donation Dialog
        if (showAddDialog) {
            DonationDialog(
                donation = selectedDonation,
                onDismiss = {
                    showAddDialog = false
                    selectedDonation = null
                },
                onSave = { newDonation ->
                    if (selectedDonation != null) {
                        // Update existing donation
                        donationViewModel.updateDonation(
                            selectedDonation!!.copy(
                                date = newDonation.date,
                                location = newDonation.location,
                                time = newDonation.time,
                                weight = newDonation.weight,
                                imageResId = newDonation.imageResId
                            )
                        )
                    } else {
                        // Add new donation
                        donationViewModel.insertDonation(newDonation)
                    }
                    showAddDialog = false
                    selectedDonation = null
                }
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun DonationHistoryItem(
    donation: Donation,
    onShowDetails: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donation Image (show only if resource is valid)
            val validImage = donation.imageResId != 0 &&
                donation.imageResId in listOf(
                    R.drawable.impact_foodbanks,
                    R.drawable.impact_donations,
                    R.drawable.impact_families,
                    R.drawable.food,
                    R.drawable.impact_volunters,
                    R.drawable.transfer
                )
            if (validImage) {
                Image(
                    painter = painterResource(id = donation.imageResId),
                    contentDescription = "Donation at ${donation.location}",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Donation details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = donation.date, fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = donation.location, fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = donation.time, fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Balance,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${donation.weight} kg", fontSize = 14.sp)
                }
            }

            // Action buttons
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Share button
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PrimaryGreen
                    )
                }

                // Edit button
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = PrimaryGreen
                    )
                }

                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }

                // Details button
                Button(
                    onClick = onShowDetails,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Show Details",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DonationsLineChart(
    modifier: Modifier = Modifier,
    data: List<Int> = listOf(2, 6, 8, 7, 10, 8, 6, 7, 9, 14, 8)
) {
    val maxValue = data.maxOrNull() ?: 0

    Canvas(
        modifier = modifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val xStep = canvasWidth / (data.size - 1)
        val yStep = canvasHeight / maxValue

        // Draw x-axis
        drawLine(
            color = Color.Gray,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Draw y-axis
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Draw line chart
        val path = Path()
        path.moveTo(0f, canvasHeight - data[0] * yStep)

        for (i in 1 until data.size) {
            path.lineTo(i * xStep, canvasHeight - data[i] * yStep)
        }

        drawPath(
            path = path,
            color = PrimaryGreen,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.cornerPathEffect(5.dp.toPx())
            )
        )

        // Draw points
        for (i in data.indices) {
            drawCircle(
                color = PrimaryGreen,
                radius = 3.dp.toPx(),
                center = Offset(i * xStep, canvasHeight - data[i] * yStep)
            )
        }

        // Draw x-axis labels
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug")
        for (i in months.indices) {
            if (i < data.size) {
                drawContext.canvas.nativeCanvas.drawText(
                    months[i],
                    i * xStep,
                    canvasHeight + 15.dp.toPx(),
                    Paint().apply {
                        textSize = 10.dp.toPx()
                        color = android.graphics.Color.GRAY
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDialog(
    donation: Donation? = null,
    onDismiss: () -> Unit,
    onSave: (Donation) -> Unit
) {
    val isEditing = donation != null

    // State for form fields
    var date by remember { mutableStateOf(donation?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var location by remember { mutableStateOf(donation?.location ?: "") }
    var time by remember { mutableStateOf(donation?.time ?: SimpleDateFormat("h:mm a", Locale.US).format(Date())) }
    var weight by remember { mutableStateOf(donation?.weight?.toString() ?: "") }

    // Image selection would be more complex in a real app
    val imageResId = donation?.imageResId ?: R.drawable.impact_donations

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Donation" else "Add Donation") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    if (location.isNotBlank() && weight.isNotBlank()) {
                        val weightFloat = weight.toFloatOrNull() ?: 0f

                        val newDonation = Donation(
                            id = donation?.id ?: 0,
                            date = date,
                            location = location,
                            time = time,
                            weight = weightFloat,
                            imageResId = imageResId
                        )

                        onSave(newDonation)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Extension function to collect Flow in Compose
@Composable
fun <T> Flow<T>.collectAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }
    LaunchedEffect(this) {
        this@collectAsState.collect { value ->
            state.value = value
        }
    }
    return state
}

// For using this screen in MainActivity
@Composable
fun DonationHistoryApp() {
    MaterialTheme {
        val donationViewModel: DonationViewModel = viewModel()
        DonationHistoryScreen(donationViewModel)
    }
}
