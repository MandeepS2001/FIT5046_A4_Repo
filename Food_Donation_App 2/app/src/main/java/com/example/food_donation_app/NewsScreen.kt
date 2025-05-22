package com.example.food_donation_app



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food_donation_app.ui.theme.PrimaryGreen
import com.example.food_donation_app.components.BottomNavigationBar
import com.example.food_donation_app.components.BottomNavItem
import java.text.SimpleDateFormat
import java.util.*

// Data class for news items
data class NewsItem(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val imageResId: Int,
    val publishDate: String,
    val category: String,
    val author: String = "Food Donation Network"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    onBackClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    // Static news data
    val newsList = remember { getStaticNewsList() }
    var selectedNewsItem by remember { mutableStateOf<NewsItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Food Donation News",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
                currentRoute = "news",
                onItemClick = { onNavigate(it.route) }
            )
        }
    ) { paddingValues ->
        if (selectedNewsItem == null) {
            // News List View
            NewsListView(
                newsList = newsList,
                onNewsClick = { newsItem ->
                    selectedNewsItem = newsItem
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            // News Detail View
            NewsDetailView(
                newsItem = selectedNewsItem!!,
                onBackClick = { selectedNewsItem = null },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun NewsListView(
    newsList: List<NewsItem>,
    onNewsClick: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Latest Food Donation News",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Stay updated with the latest news from food banks and donation initiatives",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(newsList) { newsItem ->
            NewsCard(
                newsItem = newsItem,
                onClick = { onNewsClick(newsItem) }
            )
        }
    }
}

@Composable
fun NewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // News Image
            Image(
                painter = painterResource(id = newsItem.imageResId),
                contentDescription = newsItem.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // News Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Category and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = newsItem.category,
                            color = PrimaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = newsItem.publishDate,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = newsItem.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Summary
                Text(
                    text = newsItem.summary,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By ${newsItem.author}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { /* Handle share */ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
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
fun NewsDetailView(
    newsItem: NewsItem,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Back Button Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryGreen
                )
            }
            Text(
                text = "Back to News",
                color = PrimaryGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                // News Image
                Image(
                    painter = painterResource(id = newsItem.imageResId),
                    contentDescription = newsItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = newsItem.category,
                            color = PrimaryGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = newsItem.publishDate,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = newsItem.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Author
                Text(
                    text = "By ${newsItem.author}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { isFavorite = !isFavorite },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFavorite) Color.Red else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFavorite) "Saved" else "Save")
                    }

                    Button(
                        onClick = { /* Handle share */ },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content
                Text(
                    text = newsItem.content,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Static news data
fun getStaticNewsList(): List<NewsItem> {
    return listOf(
        NewsItem(
            id = 1,
            title = "Local Food Bank Receives Record Donation of 50,000 Meals",
            summary = "The Central City Food Bank announced its largest single donation in history, providing enough food to serve 50,000 meals to families in need.",
            content = """
The Central City Food Bank has received its largest donation in the organization's 25-year history. The donation, valued at over $200,000, includes non-perishable food items, fresh produce, and frozen meals that will help feed thousands of families in our community.

"This incredible generosity comes at a critical time when food insecurity is at an all-time high," said Maria Rodriguez, Executive Director of the Central City Food Bank. "We're seeing a 40% increase in demand compared to last year, and donations like this make it possible for us to meet the growing need."

The donation was made possible through a partnership with several local businesses and a matching grant from the Community Foundation. The food bank plans to distribute the items over the next three months, with priority given to families with children and elderly residents.

Volunteers are needed to help sort and distribute the donations. Anyone interested in volunteering can visit the food bank's website or call their volunteer coordinator.
            """.trimIndent(),
            imageResId = R.drawable.impact_foodbanks,
            publishDate = "Dec 15, 2024",
            category = "Community Impact",
            author = "Sarah Johnson"
        ),
        NewsItem(
            id = 2,
            title = "New Mobile Food Pantry Launches to Serve Rural Communities",
            summary = "A innovative mobile food pantry program has launched to bring fresh groceries directly to underserved rural areas.",
            content = """
A groundbreaking mobile food pantry initiative has officially launched to address food insecurity in rural communities across the region. The program, called "Food on Wheels," operates a retrofitted truck that travels to remote areas where traditional food banks are not accessible.

The mobile pantry is equipped with refrigeration units to store fresh produce, dairy products, and frozen items. It visits different locations on a rotating schedule, ensuring that families in rural areas have regular access to nutritious food options.

"Transportation has always been a barrier for rural families trying to access food assistance," explained David Chen, program coordinator. "By bringing the food bank to them, we're removing that obstacle and ensuring no one goes hungry because of where they live."

The program serves an estimated 300 families across 15 rural communities. Local churches, community centers, and schools serve as distribution points, with volunteers helping to unload and organize the food for distribution.

Initial funding for the program came from a federal grant and private donations. Organizers are working to secure long-term funding to expand the service to additional communities.
            """.trimIndent(),
            imageResId = R.drawable.transfer,
            publishDate = "Dec 12, 2024",
            category = "Innovation",
            author = "Michael Chen"
        ),
        NewsItem(
            id = 3,
            title = "Holiday Food Drive Collects Over 100,000 Pounds of Food",
            summary = "The annual holiday food drive exceeded all expectations, collecting enough food to provide holiday meals for 5,000 families.",
            content = """
This year's holiday food drive has been the most successful in the campaign's history, collecting over 100,000 pounds of food and raising $75,000 in monetary donations. The drive, which ran from November 1st through December 10th, involved over 200 local businesses, schools, and community organizations.

The collected items will be used to create holiday meal packages for families in need. Each package contains enough food for a complete holiday dinner, including a turkey or ham, sides, and dessert ingredients.

"The response from our community has been overwhelming," said Lisa Martinez, food drive coordinator. "We've seen donations from every corner of our city, from large corporations to elementary school classes collecting canned goods."

Notable contributions included a 10,000-pound donation from a local grocery chain and a $25,000 check from the city's restaurant association. High school students organized a "Stuff the Bus" campaign that collected over 15,000 items.

Distribution of holiday meal packages begins this week, with families able to pick up their packages at designated locations throughout the city. Delivery services are also available for elderly and disabled residents.
            """.trimIndent(),
            imageResId = R.drawable.impact_families,
            publishDate = "Dec 10, 2024",
            category = "Community Events",
            author = "Jennifer Williams"
        ),
        NewsItem(
            id = 4,
            title = "Grocery Store Rescue Program Saves 2 Million Pounds of Food",
            summary = "A innovative program partnering with local grocery stores has prevented millions of pounds of food from going to waste.",
            content = """
A innovative food rescue program has successfully diverted over 2 million pounds of food from landfills in its first year of operation. The program, called "Second Harvest," partners with local grocery stores to collect food that is still safe to eat but can no longer be sold.

The rescued food includes produce that may be slightly bruised, bakery items nearing their sell-by date, and products with damaged packaging. All food is carefully inspected and sorted before being distributed to local food banks and soup kitchens.

"This program is a win-win for everyone involved," said Robert Kim, program director. "Grocery stores reduce their waste disposal costs, the environment benefits from reduced landfill waste, and families in need receive fresh, nutritious food."

The program currently works with 45 grocery stores across the metropolitan area. Volunteers collect food daily using refrigerated trucks to ensure freshness. The rescued food is then distributed to a network of 30 partner organizations.

Plans are underway to expand the program to include restaurants and catering companies. A new sorting facility is being constructed to handle the increased volume of rescued food.
            """.trimIndent(),
            imageResId = R.drawable.food,
            publishDate = "Dec 8, 2024",
            category = "Sustainability",
            author = "Amanda Rodriguez"
        ),
        NewsItem(
            id = 5,
            title = "Student-Led Food Security Initiative Feeds 500 Families Weekly",
            summary = "University students have created a comprehensive food assistance program that serves hundreds of families every week.",
            content = """
A student-led initiative at the local university has grown into a comprehensive food security program that now serves over 500 families weekly. The program, started by a group of social work students, combines food distribution with nutrition education and community gardening.

The initiative began two years ago when students noticed that many of their peers were struggling with food insecurity. What started as a small food pantry in a dormitory basement has evolved into a full-service program with multiple distribution sites across campus and the surrounding community.

"We realized that food insecurity doesn't just affect families - it affects students, faculty, and staff too," said program founder Emily Zhang, a senior social work major. "Our approach is holistic, addressing not just immediate hunger but long-term food security."

The program includes several components: a weekly food distribution that serves families and individuals, cooking classes that teach budget-friendly meal preparation, and community gardens where participants can grow their own produce. All services are provided free of charge.

Funding comes from student government allocations, faculty donations, and grants from local foundations. The program has become a model for other universities looking to address food insecurity on their campuses.
            """.trimIndent(),
            imageResId = R.drawable.impact_volunters,
            publishDate = "Dec 5, 2024",
            category = "Education",
            author = "Kevin Park"
        ),
        NewsItem(
            id = 6,
            title = "Corporate Partnership Establishes $1M Food Security Fund",
            summary = "A major corporation has partnered with local food banks to establish a permanent endowment fund for food security programs.",
            content = """
A major technology corporation has announced a $1 million partnership with the Regional Food Bank Network to establish a permanent endowment fund dedicated to food security programs. The fund will provide ongoing support for innovative approaches to addressing hunger in the community.

The partnership, announced at a press conference downtown, represents the largest single corporate commitment to food security in the region's history. The endowment will generate approximately $50,000 annually to support new and existing programs.

"Food security is a fundamental human right, and we're committed to being part of the solution," said CEO Patricia Williams. "This endowment ensures that our support will continue long into the future, providing stability for the organizations doing this critical work."

The fund will support several initiatives, including expansion of the mobile food pantry program, development of new food rescue partnerships, and creation of nutrition education programs. Priority will be given to programs that address the root causes of food insecurity.

A advisory committee comprised of food bank leaders, community representatives, and corporate volunteers will oversee the distribution of funds. The first grants are expected to be awarded in early 2025.
            """.trimIndent(),
            imageResId = R.drawable.impact_donations,
            publishDate = "Dec 3, 2024",
            category = "Corporate Partnership",
            author = "Thomas Anderson"
        ),
        NewsItem(
            id = 7,
            title = "Urban Farming Initiative Produces 50,000 Pounds of Fresh Vegetables",
            summary = "A community urban farming project has harvested its first major crop, producing thousands of pounds of fresh vegetables for local food banks.",
            content = """
A community urban farming initiative has completed its first full growing season, producing over 50,000 pounds of fresh vegetables that have been distributed to local food banks and community centers. The project, located on a previously vacant lot downtown, demonstrates how urban agriculture can help address food insecurity.

The urban farm spans three acres and includes raised beds, greenhouse facilities, and a composting system. Volunteers from the community, including many families who receive food assistance, help with planting, weeding, and harvesting.

"This project shows that communities can be part of the solution to their own food security challenges," said farm coordinator Maria Santos. "When people are involved in growing their own food, they develop a deeper connection to nutrition and healthy eating."

The farm grows a variety of vegetables including tomatoes, peppers, leafy greens, and root vegetables. All produce is grown using organic methods and is distributed within 24 hours of harvest to ensure maximum freshness and nutritional value.

Educational programs at the farm teach participants about gardening, composting, and food preservation. Children's programs introduce young people to agriculture and healthy eating habits.

The success of the pilot project has led to plans for three additional urban farms in different neighborhoods. The city council has allocated funds to support the expansion.
            """.trimIndent(),
            imageResId = R.drawable.impact_families,
            publishDate = "Nov 30, 2024",
            category = "Urban Agriculture",
            author = "Carlos Mendez"
        ),
        NewsItem(
            id = 8,
            title = "Food Bank Launches Nutrition Education Program for Families",
            summary = "A comprehensive nutrition education program helps families make the most of their food assistance while learning healthy cooking skills.",
            content = """
The Riverside Food Bank has launched a comprehensive nutrition education program designed to help families maximize the nutritional value of their food assistance. The program combines cooking demonstrations, meal planning workshops, and one-on-one nutrition counseling.

The eight-week program covers topics including budget-friendly meal planning, understanding nutrition labels, food safety, and cooking techniques for healthy meals. Participants receive take-home materials and recipes specifically designed for ingredients commonly available at food banks.

"We've learned that simply providing food isn't enough," explained nutritionist Dr. Rachel Green, who developed the curriculum. "People need the knowledge and skills to make the most of what they receive and to make healthy choices within their budget constraints."

The program is offered in both English and Spanish, with childcare provided during classes. Transportation assistance is available for participants who need it. Each class includes hands-on cooking experience, with participants preparing and sharing a meal together.

Initial results are promising, with participants reporting increased confidence in cooking and improved eating habits. Many participants have continued to attend advanced workshops and have become peer educators in their communities.

The program is funded through a combination of federal grants and private donations. Plans are in place to expand to additional food bank locations throughout the region.
            """.trimIndent(),
            imageResId = R.drawable.impact_foodbanks,
            publishDate = "Nov 28, 2024",
            category = "Health & Nutrition",
            author = "Dr. Rachel Green"
        ),
        NewsItem(
            id = 9,
            title = "Thanksgiving Distribution Serves 3,000 Families Complete Holiday Meals",
            summary = "The annual Thanksgiving food distribution provided complete holiday meals to thousands of families across the metropolitan area.",
            content = """
The annual Thanksgiving food distribution successfully provided complete holiday meals to over 3,000 families across the metropolitan area. The event, coordinated by a coalition of food banks and community organizations, required months of planning and hundreds of volunteers.

Each family received a complete Thanksgiving dinner including a turkey, stuffing, cranberry sauce, vegetables, and pumpkin pie. Fresh produce was also included to ensure families had healthy options beyond the holiday meal.

"Thanksgiving is about gratitude and community, and this distribution embodies both of those values," said event coordinator James Peterson. "Seeing families leave with everything they need for a traditional holiday meal is incredibly meaningful."

The distribution required careful logistics, with volunteers beginning setup at 5 AM to prepare for the 9 AM opening. Cars lined up for blocks as families arrived to collect their meals. The entire distribution was completed in under four hours thanks to the well-organized volunteer effort.

Special accommodations were made for elderly and disabled residents, with home delivery services provided to those unable to travel to distribution sites. Volunteer drivers delivered meals to over 200 homebound individuals.

The event was funded through a combination of corporate sponsorships, individual donations, and federal emergency food assistance programs. Planning for next year's distribution begins immediately, with organizers hoping to serve even more families.
            """.trimIndent(),
            imageResId = R.drawable.impact_volunters,
            publishDate = "Nov 25, 2024",
            category = "Holiday Programs",
            author = "Rebecca Johnson"
        ),
        NewsItem(
            id = 10,
            title = "Food Recovery Network Expands to Combat Campus Food Waste",
            summary = "A student-led organization is expanding its food recovery efforts to reduce waste in university dining halls while feeding hungry students.",
            content = """
The Food Recovery Network chapter at Metro University has expanded its operations to recover food from all campus dining facilities, preventing thousands of pounds of food waste while providing meals for food-insecure students. The student-led organization has become a model for other universities nationwide.

The program works with dining services to collect surplus food that would otherwise be discarded. Volunteers pick up the food at the end of each day and deliver it to the campus food pantry and local homeless shelters. All food safety protocols are strictly followed to ensure the donated food is safe for consumption.

"College students face food insecurity at higher rates than many people realize," said chapter president Alex Thompson. "Our program addresses both food waste and hunger on campus - it's a solution that benefits everyone."

In its first year of expanded operations, the program has recovered over 25,000 pounds of food, equivalent to approximately 20,000 meals. The recovered food includes fresh produce, prepared meals, and bakery items that would have otherwise been thrown away.

The success of the program has led to partnerships with off-campus restaurants and grocery stores. The organization has also launched an educational campaign to raise awareness about food waste and food insecurity among students.

The program has received recognition from the university administration and has secured funding for a dedicated coordinator position. Plans are underway to help establish similar programs at other universities in the region.
            """.trimIndent(),
            imageResId = R.drawable.impact_donations,
            publishDate = "Nov 22, 2024",
            category = "Campus Initiatives",
            author = "Alex Thompson"
        )
    )
}