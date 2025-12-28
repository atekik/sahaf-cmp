# Sahaf 📚

Sahaf (Turkish for "second-hand bookseller") is a cross-platform mobile application for Android and
iOS, designed to create a local community for sharing and borrowing Turkish books. Built with Kotlin
Multiplatform and Compose Multiplatform, the app enables readers to discover, share, and borrow
books within their local area.

## ✨ Features

### 🔐 User Management

- **Google Authentication:** Secure sign-in and registration using Firebase Auth
- **User Profiles:** Profile management with picture support
- **Reader Statistics:** Track followers, following, and active listings

### 📖 Book Listings

- **Google Books Integration:** Rich book metadata including titles, authors, covers, and
  descriptions
- **Multiple Delivery Methods:**
    - ☕ Local Pickup
    - 📦 Shipping
    - ☕ & 📦 Both options
- **Listing Management:** Create, update, and delete book listings
- **View Tracking:** Monitor interest in your listings

### 🗺️ Location-Based Discovery

- **Zipcode Entry:** Custom numeric keypad for easy zipcode input
- **Proximity Search:** Find books within 50km radius (geofencing)
- **Shipping Filter:** Discover books available for shipping nationwide
- **Local Connections:** Connect with readers in your area

### 🎨 User Interface

- **Material Design 3:** Modern, beautiful UI with dynamic theming
- **Custom Color Scheme:** Warm, book-inspired color palette
- **Responsive Layout:** Optimized for various screen sizes
- **Smooth Animations:** Polished user experience with transitions

### 📱 Home Feed

- **Personalized Feed:** See books with shipping options
- **Relative Timestamps:** "5 minutes ago", "Yesterday at 3:45 PM", etc.
- **Book Details:** Cover images, author info, descriptions, and view counts
- **Bottom Navigation:** Easy access to Home, Notifications, Search, and Book Store

## 🏗️ Architecture

### Project Structure

```
Sahaf/
├── composeApp/          # Shared Kotlin Multiplatform code
│   ├── src/
│   │   ├── commonMain/  # Shared business logic and UI
│   │   ├── androidMain/ # Android-specific implementations
│   │   └── iosMain/     # iOS-specific implementations
├── androidApp/          # Android application module
├── iosApp/              # iOS application (Xcode project)
├── signin/              # Authentication module
└── utils/               # Shared utility functions
```

### Tech Stack

**Frontend:**

- **Kotlin Multiplatform** - Share code across Android and iOS
- **Compose Multiplatform** - Declarative UI framework
- **Material 3** - Modern design system
- **Coil 3** - Image loading
- **Jetpack Navigation** - Navigation component

**Architecture:**

- **MVI (Model-View-Intent)** - Using Orbit MVI
- **Clean Architecture** - Separation of concerns with use cases
- **Dependency Injection** - Koin for DI
- **Repository Pattern** - Data layer abstraction

**Backend Integration:**

- **Ktor Client** - HTTP client for API calls
- **kotlinx.serialization** - JSON serialization
- **kotlinx-datetime** - Date/time handling

**Data Persistence:**

- **DataStore** - Modern data storage solution
- **Preferences** - Key-value storage

**Authentication:**

- **Firebase Auth** - Secure authentication
- **Google Sign-In** - OAuth integration

## 📦 Key Dependencies

```toml
[versions]
kotlin = "2.2.0"
compose-multiplatform = "1.9.0"
koin = "3.5.3"
orbit = "10.0.0"
ktor = "2.3.12"
kotlinx-datetime = "0.6.1"
data-store = "1.2.0"
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog or later
- **Xcode** 15+ (for iOS development)
- **JDK** 17+
- **Kotlin** 2.2.0+

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/Sahaf.git
   cd Sahaf
   ```

2. **Configure Firebase:**
    - Add `google-services.json` to `androidApp/`
    - Add `GoogleService-Info.plist` to `iosApp/iosApp/`

3. **Update Server URL:**
    - Edit `composeApp/src/commonMain/kotlin/dev/ktekik/sahaf/cloud/BookApi.kt`
    - Change `baseUrl` from `http://192.168.68.67:8080` to your server URL

4. **Build and Run:**

   **Android:**
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

   **iOS:**
   ```bash
   cd iosApp
   pod install
   open iosApp.xcworkspace
   ```

## 🧪 Testing

Run unit tests:

```bash
./gradlew :composeApp:testDebugUnitTest
```

Run specific test:

```bash
./gradlew :composeApp:testDebugUnitTest --tests "dev.ktekik.sahaf.utils.TimeUtilsTest"
```

## 📡 API Integration

### Endpoints

The app integrates with a Ktor server backend:

- `GET /listings/shipping` - Fetch listings with shipping options
- `GET /listings/{readerId}` - Get user's listings
- `POST /listings` - Create or update a listing
- `DELETE /listings/{listingId}` - Delete a listing
- `GET /readers/{readerId}` - Get reader information
- `POST /readers` - Create or update reader profile

### Data Models

**BookListing:**

```kotlin
data class BookListing(
    val readerId: Uuid,
    val book: Book,              // Full book object with Google Books data
    val createdAt: Instant,
    val updatedAt: Instant,
    val deliveryMethod: DeliveryMethod,
    val likes: List<String>,
    val viewCount: Int,
    val listingUuid: Uuid?,
    val readersContacted: Set<String>,
    val zipcode: String,
)
```

**Book:**

```kotlin
data class Book(
    val bookId: String?,
    val isbn: String?,           // ISBN-10
    val infoLink: String,
    val title: String,
    val authors: List<String>,   // Multiple authors
    val pageCount: Int?,
    val publishedDate: String,
    val categories: List<String>?,
    val cover: ImageLinks?,      // Multiple resolution covers
    val language: String?,
    val textSnippet: String?,
)
```

## 🎨 Design System

### Colors

The app uses a warm, book-inspired color palette:

**Light Theme:**

- Primary: `#825500` (Brown)
- Primary Container: `#FFDDB3` (Light Orange)
- Secondary: `#6F5B40` (Tan)
- Background: `#FFFBFF` (Off-white)

**Dark Theme:**

- Primary: `#FFB951` (Gold)
- Primary Container: `#633F00` (Dark Brown)
- Surface: `#1F1B16` (Dark Brown)

### Typography

Uses Inter font family with semantic text styles for consistency.

## 🛠️ Development

### Code Organization

```
composeApp/src/commonMain/kotlin/dev/ktekik/sahaf/
├── cloud/           # API clients and network layer
├── datastore/       # Local data persistence
├── di/              # Dependency injection modules
├── fts/             # First-time setup screens
├── home/            # Home screen and feed
├── models/          # Data models
├── navigation/      # Navigation logic
├── reader/          # Reader profile features
├── theming/         # Design system (colors, typography)
├── usecases/        # Business logic use cases
└── utils/           # Utility functions
```

### Adding a New Feature

1. Create data models in `models/`
2. Add API client in `cloud/`
3. Create use case in `usecases/`
4. Register dependencies in `di/CommonModule.kt`
5. Implement UI in appropriate screen package
6. Add navigation if needed

## 📝 Utilities

### TimeUtils

Format relative timestamps:

```kotlin
getRelativeTimeString(instant: Instant): String
// Returns: "5 minutes ago", "Today at 3:45 PM", "Yesterday at 9:00 AM", "on 01/15"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Kotlin Multiplatform** - For enabling true cross-platform development
- **Compose Multiplatform** - For beautiful declarative UI
- **Google Books API** - For rich book metadata
- **Firebase** - For authentication services
- **Orbit MVI** - For predictable state management

## 📧 Contact

For questions or feedback, please open an issue on GitHub.

---

Built with ❤️ for the Turkish book-reading community
