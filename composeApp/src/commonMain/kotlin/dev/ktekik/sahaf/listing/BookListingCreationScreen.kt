package dev.ktekik.sahaf.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.DeliveryMethod
import org.jetbrains.compose.resources.stringResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.back
import sahaf.composeapp.generated.resources.book_details
import sahaf.composeapp.generated.resources.book_information
import sahaf.composeapp.generated.resources.book_listing_button_looks_good
import sahaf.composeapp.generated.resources.by_author
import sahaf.composeapp.generated.resources.cd_book_cover
import sahaf.composeapp.generated.resources.delivery_local_pickup
import sahaf.composeapp.generated.resources.delivery_shipping
import sahaf.composeapp.generated.resources.label_description
import sahaf.composeapp.generated.resources.label_isbn
import sahaf.composeapp.generated.resources.label_pages
import sahaf.composeapp.generated.resources.label_published
import sahaf.composeapp.generated.resources.label_publishers
import sahaf.composeapp.generated.resources.label_subjects
import sahaf.composeapp.generated.resources.listing_delivery_method


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookListingCreationScreen(
    book: Book,
    onContinuePressed: (book: Book, MutableMap<DeliveryMethod, Boolean>) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.book_details),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Text(
                        text = stringResource(Res.string.back),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onBackPressed() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Book Cover Section
            BookCoverSection(book = book)

            Spacer(modifier = Modifier.height(16.dp))

            // Book Info Section
            BookInfoSection(book = book)

            Spacer(modifier = Modifier.height(16.dp))

            val map = remember { mutableMapOf<DeliveryMethod, Boolean>() }
            var isContinueButtonEnabled by remember { mutableStateOf(false) }

            DeliveryInformation(map) {
                map[it.first] = it.second
                isContinueButtonEnabled = map.values.contains(true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SubmitButton(
                isContinueButtonEnabled,
                stringResource(Res.string.book_listing_button_looks_good)
            ) {
                onContinuePressed(book, map)
            }
        }
    }
}

@Composable
internal fun BookCoverSection(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Cover
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(0.67f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (book.cover?.medium != null) {
                    AsyncImage(
                        model = book.cover.medium,
                        contentDescription = stringResource(Res.string.cd_book_cover),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = book.title.take(1).uppercase(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Title and Authors
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (book.authors.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            Res.string.by_author,
                            book.authors.joinToString(", ")
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
internal fun BookInfoSection(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.book_information),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ISBN
        book.isbn?.let { isbn ->
            InfoRow(label = stringResource(Res.string.label_isbn), value = isbn)
        }

        // Page Count
        book.pageCount?.let { pages ->
            InfoRow(label = stringResource(Res.string.label_pages), value = pages.toString())
        }

        // Published Date
        InfoRow(label = stringResource(Res.string.label_published), value = book.publishedDate)

        // Publishers
        book.publishers?.let { publishers ->
            if (publishers.isNotEmpty()) {
                InfoRow(
                    label = stringResource(Res.string.label_publishers),
                    value = publishers.joinToString(", ")
                )
            }
        }

        // Subjects
        book.subjects?.let { subjects ->
            if (subjects.isNotEmpty()) {
                InfoRow(
                    label = stringResource(Res.string.label_subjects),
                    value = subjects.joinToString(", ")
                )
            }
        }

        // Description/Snippet
        book.snippets?.firstOrNull()?.let { snippet ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.label_description),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
internal fun InfoRow(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
internal fun SubmitButton(isEnabled: Boolean, buttonText: String, onClicked: () -> Unit) {
    Button(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 128.dp
        ).fillMaxWidth(),
        onClick = {
            onClicked()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp,
            focusedElevation = 10.dp
        ),
        shape = MaterialTheme.shapes.extraLarge,
        enabled = isEnabled,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = buttonText,
            color = LocalContentColor.current,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun DeliveryInformation(
    map: Map<DeliveryMethod, Boolean>,
    onCheckedChange: (Pair<DeliveryMethod, Boolean>) -> Unit,
) {
    Column {
        Text(
            text = stringResource(Res.string.listing_delivery_method),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        DeliveryMethodRow(
            deliveryMethodText = stringResource(Res.string.delivery_local_pickup),
            defaultValue = map[DeliveryMethod.LocalPickup] ?: false,
            onCheckedChange = {
                onCheckedChange(DeliveryMethod.LocalPickup to it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeliveryMethodRow(
            deliveryMethodText = stringResource(Res.string.delivery_shipping),
            defaultValue = map[DeliveryMethod.Shipping] ?: false,
            onCheckedChange = {
                onCheckedChange(DeliveryMethod.Shipping to it)
            }
        )
    }
}

@Composable
internal fun DeliveryMethodRow(
    deliveryMethodText: String,
    defaultValue: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
) {
    var checked by remember { mutableStateOf(defaultValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = !checked
                onCheckedChange(checked)
            }
        )
        Text(
            text = deliveryMethodText,
            modifier = Modifier.padding(start = 8.dp), // Space between checkbox and text
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}