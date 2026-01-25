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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.ktekik.barcodescanner.CameraPreview
import dev.ktekik.sahaf.models.DeliveryMethod
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.utils.getRelativeTimeString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.cd_book_cover
import sahaf.composeapp.generated.resources.cd_sahaf_logo
import sahaf.composeapp.generated.resources.cd_user_profile_picture
import sahaf.composeapp.generated.resources.delivery_local_pickup
import sahaf.composeapp.generated.resources.delivery_local_pickup_and_shipping
import sahaf.composeapp.generated.resources.delivery_shipping
import sahaf.composeapp.generated.resources.ic_book_store
import sahaf.composeapp.generated.resources.ic_home
import sahaf.composeapp.generated.resources.ic_notification
import sahaf.composeapp.generated.resources.ic_search
import sahaf.composeapp.generated.resources.last_updated
import sahaf.composeapp.generated.resources.nav_book_store
import sahaf.composeapp.generated.resources.nav_home
import sahaf.composeapp.generated.resources.nav_notification
import sahaf.composeapp.generated.resources.nav_scan
import sahaf.composeapp.generated.resources.short_logo
import sahaf.composeapp.generated.resources.unknown_author

private val rainbowArray = longArrayOf(
    0xFF372780,
    0xFF2C5499,
    0xFF4F8D23,
    0xFFE8C917,
    0xFFE05D1A,
    0xFFB9231F
)

@Composable
internal fun HomeReadyScreen(currentState: HomeScreenState.ReadyState, navigationViewModel: NavigationViewModel = koinInject()) {
    var selectedNavItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            HomeBottomNavigationBar(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { paddingValues ->
        when (selectedNavItem) {
            2 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CameraPreview(modifier = Modifier) { isbn ->
                        navigationViewModel.onIsbnScanned(isbn)
                    }
                }
            }

            else -> {
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

                    val unknownAuthor = stringResource(Res.string.unknown_author)
                    val deliveryLocalPickup = stringResource(Res.string.delivery_local_pickup)
                    val deliveryShipping = stringResource(Res.string.delivery_shipping)
                    val deliveryLocalPickupAndShipping = stringResource(Res.string.delivery_local_pickup_and_shipping)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(currentState.listings.items.mapIndexed { index, listing ->
                            BookCardData(
                                title = listing.book.title,
                                author = listing.book.authors.firstOrNull() ?: unknownAuthor,
                                description = listing.book.snippets?.firstOrNull() ?: "",
                                coverColor = Color(rainbowArray[index % rainbowArray.size]),
                                coverUrl = listing.book.cover?.medium ?: listing.book.cover?.small,
                                viewCount = listing.viewCount,
                                publisher = listing.book.publishers?.firstOrNull() ?: "",
                                deliveryMethod = when (listing.deliveryMethod) {
                                    DeliveryMethod.LocalPickup -> deliveryLocalPickup
                                    DeliveryMethod.Shipping -> deliveryShipping
                                    DeliveryMethod.LocalPickupAndShipping -> deliveryLocalPickupAndShipping
                                },
                                lastUpdate = listing.updatedAt?.let { getRelativeTimeString(it) } ?: ""
                            )
                        }) { book ->
                            BookCard(book = book)
                        }
                    }
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
            contentDescription = stringResource(Res.string.cd_sahaf_logo),
            modifier = Modifier.height(48.dp)
        )

        // Profile Picture
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = stringResource(Res.string.cd_user_profile_picture),
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
    // Create painter for the book cover
    val painter = rememberAsyncImagePainter(model = book.coverUrl)

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
                    .aspectRatio(0.96f)
            ) {
                if (book.coverUrl != null) {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(Res.string.cd_book_cover),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = book.title.take(1),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
                    text = book.publisher,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.deliveryMethod,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.last_updated, book.lastUpdate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                    contentDescription = stringResource(Res.string.nav_home)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.nav_home),
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
                    contentDescription = stringResource(Res.string.nav_notification)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.nav_notification),
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
                    contentDescription = stringResource(Res.string.nav_scan)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.nav_scan),
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
                    contentDescription = stringResource(Res.string.nav_book_store)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.nav_book_store),
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
    val rating: Float = 5f,
    val coverUrl: String? = null,
    val viewCount: Int = 0,
    val deliveryMethod: String = "Local pick up & Shipping",
    val publisher: String = "",
    val lastUpdate: String = "10 mins ago"
)
