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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.models.BookListing
import org.jetbrains.compose.resources.stringResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.back
import sahaf.composeapp.generated.resources.book_details
import sahaf.composeapp.generated.resources.book_listing_contact_user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookListingReadOnlyScreen(
    bookListing: BookListing,
    onBackPressed: () -> Unit,
    onContactPressed: () -> Unit = {},
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
            BookCoverSection(book = bookListing.book)

            Spacer(modifier = Modifier.height(16.dp))

            BookInfoSection(book = bookListing.book)

            Spacer(modifier = Modifier.height(16.dp))

            SubmitButton(
                isEnabled = true,
                buttonText = stringResource(Res.string.book_listing_contact_user)
            ) {
                onContactPressed()
            }
        }
    }
}