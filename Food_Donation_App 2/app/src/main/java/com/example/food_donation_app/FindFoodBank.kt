package com.example.food_donation_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.components.BottomNavigationBar
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.example.food_donation_app.ui.theme.PrimaryGreen

// Data class for Food Bank
data class FoodBank(
    val id: Int,
    val name: String,
    val distance: String,
    val status: String,
    val address: String,
    val phone: String,
    val hours: String,
    val location: LatLng,
    val isOpen: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindFoodBankScreen(
    onNavigate: (String) -> Unit = {}
) {
    // Camera position centered around Melbourne area
    val initialPosition = LatLng(-37.8136, 144.9631)
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(initialPosition, 11f)
    }

    // Static food bank data - 10 locations around Melbourne
    val foodBanks = remember {
        listOf(
            FoodBank(
                1, "Pichu Food Bank", "0.8km", "Open",
                "123 Smith St, Melbourne VIC 3000", "(03) 1234 5678",
                "Mon-Fri: 9AM-5PM", LatLng(-37.8136, 144.9631), true
            ),
            FoodBank(
                2, "Mew Food Bank", "1.5km", "Open",
                "456 Collins St, Melbourne VIC 3000", "(03) 2345 6789",
                "Mon-Sat: 8AM-6PM", LatLng(-37.8173, 144.9668), true
            ),
            FoodBank(
                3, "Community Care Food Hub", "2.1km", "Closed",
                "789 Bourke St, Melbourne VIC 3000", "(03) 3456 7890",
                "Tue-Thu: 10AM-4PM", LatLng(-37.8140, 144.9633), false
            ),
            FoodBank(
                4, "Melbourne Food Relief", "2.8km", "Open",
                "321 Flinders St, Melbourne VIC 3000", "(03) 4567 8901",
                "Daily: 7AM-8PM", LatLng(-37.8183, 144.9671), true
            ),
            FoodBank(
                5, "St. Mary's Food Pantry", "3.2km", "Open",
                "654 Chapel St, South Yarra VIC 3141", "(03) 5678 9012",
                "Wed-Sun: 9AM-3PM", LatLng(-37.8467, 144.9857), true
            ),
            FoodBank(
                6, "North Melbourne Food Bank", "3.7km", "Closed",
                "147 Victoria St, North Melbourne VIC 3051", "(03) 6789 0123",
                "Mon-Fri: 11AM-3PM", LatLng(-37.7967, 144.9440), false
            ),
            FoodBank(
                7, "Richmond Community Kitchen", "4.1km", "Open",
                "258 Swan St, Richmond VIC 3121", "(03) 7890 1234",
                "Tue-Sat: 8AM-5PM", LatLng(-37.8197, 144.9936), true
            ),
            FoodBank(
                8, "Footscray Food Hub", "5.3km", "Open",
                "369 Barkly St, Footscray VIC 3011", "(03) 8901 2345",
                "Mon-Thu: 9AM-4PM", LatLng(-37.7986, 144.9017), true
            ),
            FoodBank(
                9, "Carlton Food Relief Centre", "4.8km", "Closed",
                "741 Lygon St, Carlton VIC 3053", "(03) 9012 3456",
                "Fri-Sun: 10AM-2PM", LatLng(-37.7981, 144.9672), false
            ),
            FoodBank(
                10, "Brighton Community Food Bank", "6.2km", "Open",
                "852 Bay St, Brighton VIC 3186", "(03) 0123 4567",
                "Mon-Wed: 8AM-6PM", LatLng(-37.9067, 144.9952), true
            )
        )
    }

    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Find Food Banks",
                        color = Color.White,
                        fontSize = 22.sp,
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
                currentRoute = "find_food_banks",
                onItemClick = { onNavigate(it.route) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map Section (60% of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                // Google Map with multiple markers
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    foodBanks.forEach { foodBank ->
                        Marker(
                            state = MarkerState(position = foodBank.location),
                            title = foodBank.name,
                            snippet = "${foodBank.status} • ${foodBank.distance}"
                        )
                    }
                }

                // Search bar overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(24.dp))
                        .height(48.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search by location") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { /* Voice search */ }) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.Gray)
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray)
                    }
                }
            }

            // List Section (40% of screen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0xFFF5F8F3))
            ) {
                // Header for the list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Food Banks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${foodBanks.size} found",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Scrollable list of food banks
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(foodBanks) { foodBank ->
                        FoodBankCard(
                            foodBank = foodBank,
                            onClick = {
                                // Handle click - could navigate to detail screen
                                // onNavigate("food_bank_detail/${foodBank.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodBankCard(
    foodBank: FoodBank,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (foodBank.isOpen) Color(0xFFE8F5E8) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = if (foodBank.isOpen) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Food bank details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = foodBank.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = foodBank.status,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (foodBank.isOpen) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                    Text(
                        text = " • ${foodBank.distance}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = foodBank.address,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Click for more info →",
                    fontSize = 12.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}