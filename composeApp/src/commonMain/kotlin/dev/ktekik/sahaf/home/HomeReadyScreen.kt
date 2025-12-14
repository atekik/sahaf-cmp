package dev.ktekik.sahaf.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.ic_book_store
import sahaf.composeapp.generated.resources.ic_home
import sahaf.composeapp.generated.resources.ic_notification
import sahaf.composeapp.generated.resources.ic_search
import sahaf.composeapp.generated.resources.short_logo

@Composable
internal fun HomeReadyScreen(currentState: HomeScreenState.ReadyState) {
    var selectedNavItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            HomeBottomNavigationBar(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            HomeTopBar(
                userName = currentState.reader.name,
                profilePictureUrl = currentState.reader.pictureURL
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(getMockRomanceBooks().toMutableList().apply {
                    addAll(getMockComedyBooks())
                    addAll(getMockPoetryBooks())
                    addAll(getMockShortStoryBooks())
                }) { book ->
                    BookCard(book = book)
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Image(
            painter = painterResource(Res.drawable.short_logo),
            contentDescription = "Sahaf Logo",
            modifier = Modifier.height(48.dp)
        )

        // Profile Picture
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            // TODO: Load profile picture from URL

            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "User Profile Picture",
                modifier = Modifier.clip(CircleShape).size(48.dp),
            )
        }
    }
}

@Composable
private fun BookCard(
    book: BookCardData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Row {
            // Book Cover
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(book.coverColor)
            ) {
                // TODO: Add book cover image
                Text(
                    text = book.title.take(1),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Book Info
            Column(
                modifier = Modifier.padding(12.dp).weight(2f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "(${book.author})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating Stars
                Row {
                    repeat(5) {
                        Text(
                            text = "⭐",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeBottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_notification),
                    contentDescription = "Notification"
                )
            },
            label = {
                Text(
                    text = "Notification",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = "Search"
                )
            },
            label = {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_book_store),
                    contentDescription = "Book Store"
                )
            },
            label = {
                Text(
                    text = "Book Store",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = Color.Transparent
            )
        )
    }
}

// Mock data classes and functions
private data class BookCardData(
    val title: String,
    val author: String,
    val description: String,
    val coverColor: Color,
    val rating: Float = 5f
)

private fun getMockPoetryBooks() = listOf(
    BookCardData("Prisoner", "Arnod Miller", "Lorem ipsum dolor sit sit", Color(0xFFB71C1C)),
    BookCardData(
        "The Words I Cannot Say",
        "Smith Brooks",
        "Lorem ipsum dolor sit sit",
        Color(0xFF880E4F)
    ),
    BookCardData("Embodied Hope", "James Alma", "Lorem ipsum dolor sit sit", Color(0xFF4A148C)),
    BookCardData(
        "The Thirteen Circle",
        "Miller Brass",
        "Lorem ipsum dolor sit sit",
        Color(0xFF311B92)
    ),
    BookCardData("My Heart", "Rachel Kalifa", "Lorem ipsum dolor sit sit", Color(0xFF1A237E))
)

private fun getMockRomanceBooks() = listOf(
    BookCardData("Meet You", "Bill Silas", "Lorem ipsum dolor sit sit", Color(0xFFFF6F00)),
    BookCardData("Moonstruck", "Amber Love", "Lorem ipsum dolor sit sit", Color(0xFF0D47A1)),
    BookCardData("Sunset Kiss Last", "Harry Smith", "Lorem ipsum dolor sit sit", Color(0xFFE65100)),
    BookCardData("My Life", "Ivan Toney", "Lorem ipsum dolor sit sit", Color(0xFF1B5E20)),
    BookCardData("Always Unloved", "Mason Gibbs", "Lorem ipsum dolor sit sit", Color(0xFF827717))
)

private fun getMockShortStoryBooks() = listOf(
    BookCardData("Short Story", "Robert Graham", "Lorem ipsum dolor sit sit", Color(0xFF4E342E)),
    BookCardData(
        "Dream from Nepal",
        "Mikel Oblack",
        "Lorem ipsum dolor sit sit",
        Color(0xFF006064)
    ),
    BookCardData(
        "Two Flared Nostrils",
        "Tom Story",
        "Lorem ipsum dolor sit sit",
        Color(0xFF37474F)
    ),
    BookCardData(
        "The Tell-Tale Heart",
        "Edglar Pok",
        "Lorem ipsum dolor sit sit",
        Color(0xFF263238)
    ),
    BookCardData("Riptides", "Williams James", "Lorem ipsum dolor sit sit", Color(0xFF3E2723))
)

private fun getMockComedyBooks() = listOf(
    BookCardData("Love Maybe", "Ena Missirit", "Lorem ipsum dolor sit sit", Color(0xFFAD1457)),
    BookCardData("Circumcised at 17", "Max Jacks", "Lorem ipsum dolor sit sit", Color(0xFF6A1B9A)),
    BookCardData(
        "Little Betty Finds a Condom",
        "Shakes",
        "Lorem ipsum dolor sit sit",
        Color(0xFF4527A0)
    ),
    BookCardData(
        "It Sounded Better in My Head",
        "Water P",
        "Lorem ipsum dolor sit sit",
        Color(0xFF283593)
    ),
    BookCardData("Ditched Again", "James Milner", "Lorem ipsum dolor sit sit", Color(0xFF1565C0))
)
