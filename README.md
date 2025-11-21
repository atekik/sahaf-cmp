# Sahaf

Sahaf is a mobile application for both Android and iOS, designed to create a community for sharing and borrowing Turkish books.

## Features

*   **Book Sharing:** Users can list their Turkish books for others to borrow.
*   **Book Discovery:** Users can find and reserve books from others in their area.
*   **User Authentication:** Secure sign-in and registration using Google authentication.
*   **Location-Based:** The app uses zipcode information to connect users locally.

## Project Structure

This is a Kotlin Multiplatform project built with Jetpack Compose.

*   `:composeApp`: Contains the shared UI and business logic for both Android and iOS applications, written in Compose Multiplatform.
*   `:androidApp`: The Android-specific application module.
*   `:signin`: A module to handle user authentication.
*   `:utils`: A module for shared utility functions.
