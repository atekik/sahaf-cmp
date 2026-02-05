package dev.ktekik.sahaf.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.models.DeliveryMethod
import dev.ktekik.sahaf.models.toPairMap
import org.jetbrains.compose.resources.stringResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.back
import sahaf.composeapp.generated.resources.book_details
import sahaf.composeapp.generated.resources.book_listing_update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookListingUpdateScreen(
    bookListing: BookListing,
    onSubmitPressed: (bookListing: BookListing, MutableMap<DeliveryMethod, Boolean>) -> Unit,
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
            BookCoverSection(book = bookListing.book)

            Spacer(modifier = Modifier.height(16.dp))

            // Book Info Section
            BookInfoSection(book = bookListing.book)

            Spacer(modifier = Modifier.height(16.dp))

            val map = remember { bookListing.deliveryMethod.toPairMap() }
            var isContinueButtonEnabled by remember { mutableStateOf(false) }

            DeliveryInformation(map) {
                map[it.first] = it.second
                isContinueButtonEnabled = bookListing.deliveryMethod.toPairMap() != map &&
                        map.values.contains(true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SubmitButton(
                isContinueButtonEnabled,
                stringResource(Res.string.book_listing_update)
            ) {
                onSubmitPressed(bookListing, map)
            }
        }
    }
}