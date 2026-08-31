# ProfileApp

A modern Android profile management application built with Jetpack Compose and Clean Architecture principles.

## Features

### Profile Management
- ✅ **Create New Profiles** - Add user profiles with name, email, phone, and photo
- 📝 **Edit Profiles** - Update existing profile information
- 🗑️ **Delete Profiles** - Remove profiles from the database
- 📸 **Photo Upload** - Select profile pictures from device gallery
- 📋 **Profile List** - View all profiles in a scrollable list

### User Experience
- 🎨 **Material Design 3** - Modern, beautiful UI with Material You components
- 🌓 **Edge-to-Edge Display** - Immersive full-screen experience
- ⚡ **Loading States** - Smooth loading indicators for async operations
- ✨ **Success Messages** - Snackbar notifications for successful actions
- 🔔 **Real-time Updates** - Instant UI updates after create/update/delete operations

### Data Validation
- ✏️ **Form Validation** - Real-time input validation with error messages
  - Name: Required, minimum 2 characters
  - Email: Required, valid email format
  - Phone: Required, exactly 10 digits with max length enforcement
  - Photo: Required for profile creation
- 🚫 **Inline Error Display** - Field-level error messages with visual feedback

### Technical Features
- 💾 **Offline-First** - Room database for local data persistence
- 🔄 **Background Sync** - WorkManager for data synchronization
- 🏗️ **Clean Architecture** - Separation of concerns with multi-module design
- 🧩 **Dependency Injection** - Hilt for seamless DI across modules
- 🧭 **Type-Safe Navigation** - Kotlin Serialization for navigation arguments
- 🔄 **State Management** - Unidirectional data flow with StateFlow
- ⚠️ **Error Handling** - Comprehensive error handling with network/server/unknown error states

## Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material Design 3** - Design system and components

### Architecture & Dependencies
- **Clean Architecture** - Multi-module project structure with clear layer separation
- **MVVM Pattern** - ViewModel with StateFlow for reactive state management
- **Hilt** - Dependency injection framework
- **Room Database** - Local SQLite database with DAO pattern
- **WorkManager** - Background sync and scheduled tasks
- **Kotlin Coroutines & Flow** - Asynchronous programming and reactive streams
- **Kotlin Serialization** - Type-safe data serialization
- **Navigation Compose** - Type-safe navigation with arguments
- **StateFlow** - Reactive state management for UI
- **SharedFlow** - One-time UI effects (snackbars, navigation)

## Project Structure

```
ProfileApp/
├── app/                      # Main application module
├── feature/
│   ├── profile/              # Profile detail feature
│   └── profileList/          # Profile list feature
├── core/
│   ├── data/                 # Data layer implementations
│   ├── database/             # Room database
│   ├── network/              # Network layer
│   └── sync/                 # Data synchronization
└── common/
    └── profile/              # Shared profile domain models
```

## Requirements

- **Android Studio** Koala | 2024.1.1 or later
- **Minimum SDK** 24 (Android 7.0)
- **Target SDK** 37
- **JDK** 11 or higher

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ParthibanAndroid/ProfileApp.git
   cd ProfileApp
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## User Flow

### Creating a Profile
1. Launch app → Profile List screen displays
2. Tap FAB (Floating Action Button) with "+" icon
3. Navigate to "Save Profile" screen
4. Fill in required fields:
   - Tap profile image placeholder to select photo from gallery
   - Enter name (min 2 characters)
   - Enter valid email address
   - Enter 10-digit phone number
5. Tap "Save" button
6. On success, navigate back with "Profile Created" message
7. New profile appears in the list

### Editing a Profile
1. From Profile List, tap any profile card
2. Navigate to "Profile Details" screen with pre-filled data
3. Modify any field (name, email, phone, or photo)
4. Tap "Update" button
5. On success, navigate back with "Profile Updated" message
6. Changes reflect in the list

### Deleting a Profile
1. From Profile List, tap profile to edit
2. On "Profile Details" screen, tap "Delete" button
3. Profile removed from database
4. Navigate back with "Profile Deleted" message
5. Profile no longer appears in list

### Validation & Error Handling
- **Real-time validation** - Errors appear as you type
- **Visual feedback** - Red border and error text for invalid fields
- **Inline error messages** - Specific guidance for each field
- **Prevent invalid submission** - Only valid forms can be saved
- **Network errors** - Graceful handling with retry options

## Architecture

This app follows Clean Architecture and MVVM patterns with:

- **Presentation Layer** - Jetpack Compose UI with ViewModels
- **Domain Layer** - Use cases and business logic
- **Data Layer** - Repository pattern with local database

### Key Architectural Decisions

- **Multi-module structure** for separation of concerns and build optimization
- **Unidirectional data flow** for predictable state management
- **Dependency injection** with Hilt for testability
- **Type-safe navigation** using Kotlin Serialization

### Implemented Patterns & Best Practices

#### State Management
- **MVI-inspired pattern** - Events, State, and Effects
- **StateFlow** for UI state - Reactive, lifecycle-aware state holder
- **SharedFlow** for one-time events - Navigation and snackbar messages
- **SavedStateHandle** - State restoration across process death

#### Form Handling
- **Real-time validation** - Validate on each input change
- **Error state mapping** - Sealed interfaces for type-safe error handling
- **Field-specific errors** - Individual error messages per input field
- **Max length enforcement** - Prevent invalid input at UI level (e.g., 10-digit phone)

#### Navigation
- **Type-safe routes** - Kotlin Serialization for compile-time safety
- **Back stack management** - Proper handling of back press with BackHandler
- **Result passing** - Success messages via SavedStateHandle
- **Argument handling** - Nullable profileId for create/edit modes

#### Data Layer
- **Repository pattern** - Abstract data sources from business logic
- **Background sync** - WorkManager for periodic data synchronization
- **Error wrapping** - Network/server/unknown error categorization
- **Single source of truth** - Room database as primary data source

## License

This project is for learning purposes.

## Author

Parthiban Sellamuthu

---

**Note:** This is a learning project demonstrating modern Android development practices.
