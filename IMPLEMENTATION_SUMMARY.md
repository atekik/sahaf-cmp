# BookApi Implementation Summary

## Overview

Successfully implemented `BookApi` for the Sahaf mobile app, mirroring the `/listings/shipping`
endpoint from the ktor server.

## Files Created

### 1. **BookListing Model** (
`composeApp/src/commonMain/kotlin/dev/ktekik/sahaf/models/BookListing.kt`)

- `BookListing` data class with all fields from the server model
- `DeliveryMethod` enum (LocalPickup, Shipping, LocalPickupAndShipping)
- `CursorPagedListings` wrapper for paginated responses with cursor

### 2. **BookApi Interface** (`composeApp/src/commonMain/kotlin/dev/ktekik/sahaf/cloud/BookApi.kt`)

- Interface defining `getListingsWithShipping()` method
- Implementation supporting query parameters:
    - `readerId`: Required - The reader making the request
    - `excludeSelf`: Optional (default: true) - Exclude own listings
    - `limit`: Optional - Page size limit
    - `before`: Optional - Cursor for pagination (Instant timestamp)
- Returns `Flow<ApiResult<CursorPagedListings>>`
- Uses same error handling pattern as `ReaderApi`

### 3. **Use Case** (
`composeApp/src/commonMain/kotlin/dev/ktekik/sahaf/usecases/FetchListingsWithShippingUseCase.kt`)

- `FetchListingsParams` data class for parameters
- `FetchListingsResult` sealed class for success/error states
- `FetchListingsWithShippingUseCase` implementing the UseCase pattern
- Transforms `ApiResult` to domain-specific `FetchListingsResult`

## Dependencies Added

- **kotlinx-datetime** (version 0.6.1) added to `gradle/libs.versions.toml`
- Added to `composeApp/build.gradle.kts` common dependencies

## Dependency Injection

- Registered `BookApi` factory in `CommonModule.kt`
- Registered `FetchListingsWithShippingUseCase` factory in `CommonModule.kt`

## Usage Example

```kotlin
class MyViewModel(
    private val fetchListingsUseCase: FetchListingsWithShippingUseCase
) : ViewModel() {
    
    fun loadShippingListings(readerId: String) {
        viewModelScope.launch {
            fetchListingsUseCase.execute(
                FetchListingsParams(
                    readerId = readerId,
                    excludeSelf = true,
                    limit = 20,
                    before = null
                )
            ).collect { result ->
                when (result) {
                    is FetchListingsResult.Success -> {
                        // Handle listings: result.data.items
                        // Next page cursor: result.data.nextCursor
                    }
                    is FetchListingsResult.Error -> {
                        // Handle error: result.message
                    }
                }
            }
        }
    }
}
```

## Server Endpoint Reference

- **Endpoint**: `GET /listings/shipping`
- **Query Parameters**:
    - `readerId` (required): UUID string
    - `excludeSelf` (optional): "true"/"false"
    - `limit` (optional): Integer (default: 20)
    - `before` (optional): ISO-8601 timestamp string

## Notes

- Base URL is hardcoded to `http://192.168.68.67:8080` (same as ReaderApi)
- The API follows the same patterns as the existing `ReaderApi`
- Uses Kotlin's UUID (`kotlin.uuid.Uuid`) instead of Java's UUID
- Cursor-based pagination is supported for infinite scrolling
