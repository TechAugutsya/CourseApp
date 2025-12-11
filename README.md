# Course Management App

A modern Android application for managing courses with offline-first architecture, built using
Jetpack Compose and Clean Architecture principles.

## 📋 Project Overview

This app allows users to create, manage, and organize courses with features like:

- Create, read, update, and delete courses
- Search and filter courses by category
- Custom or API-fetched categories
- Automatic score calculation based on course properties
- Full offline functionality with local data persistence
- Modern Material 3 UI design

## ✨ Key Features

### Course Management

- **Add Courses**: Create new courses with title, description, category, and lesson count
- **Edit Courses**: Update existing course information
- **Delete Courses**: Remove courses with confirmation dialog
- **View Details**: See comprehensive course information including timestamps

### Search & Filter

- Real-time search by course title or description
- Filter courses by category
- Combined search and filter functionality

### Categories

- Fetch categories from API on first launch
- Add custom categories while creating courses
- Automatically merge API and local categories
- Offline fallback to cached categories

### Score System

The app calculates a course score using:

```
Score = (Title Length) × (Number of Lessons)
```

Example: "Advanced Android" (16 chars) × 20 lessons = 320 points

### Offline-First

- All courses stored locally using Room Database
- Categories cached for offline access
- App works completely without internet
- Data persists across app restarts

## 🛠️ Tech Stack

### Core

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design**: Material Design 3
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Architecture & Libraries

- **Architecture**: Clean Architecture (MVVM)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Networking**: Retrofit + Moshi
- **Async**: Coroutines + Flow
- **Navigation**: Navigation Compose

### Dependencies

```gradle
- Jetpack Compose: 1.10.0
- Room: 2.8.4
- Retrofit: 2.11.0
- Moshi: 1.15.2
- Hilt: 2.57.2
- Coroutines: 1.10.2
- Navigation Compose: 2.9.6
```

## 📁 Project Structure

```
app/src/main/java/com/example/courseapp/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── db/               # Database
│   │   └── entities/         # Room Entities
│   ├── remote/               # API Interface
│   └── repository/           # Data Repositories
├── domain/
│   └── models/               # Domain Models
├── ui/
│   ├── components/           # Reusable UI Components
│   ├── screens/              # Screen Composables
│   └── theme/                # Theme & Styling
├── viewmodel/                # ViewModels
└── di/                       # Dependency Injection
```

## 🚀 Setup Instructions

### Prerequisites

- **Android Studio**: Latest version (Hedgehog or newer recommended)
- **JDK**: Version 11 or higher
- **Android SDK**: API level 36
- **Gradle**: 8.13 (included in wrapper)

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd CourseApp
```

### Step 2: Open in Android Studio

1. Launch Android Studio
2. Click **File → Open**
3. Navigate to the project directory and select it
4. Wait for Gradle sync to complete

### Step 3: Configure Mock API (Optional)

The app uses a Postman Mock Server for categories. The default endpoint is already configured:

**Base URL**: `https://8dba06d5-9a21-4376-bd5c-e9221fd1b620.mock.pstmn.io/`

**Endpoint**: `GET /categories`

**Expected Response Format**:

```json
[
  { "id": "1", "name": "Technology" },
  { "id": "2", "name": "Design" },
  { "id": "3", "name": "Business" },
  { "id": "4", "name": "Marketing" }
]
```

To use your own API:

1. Open `app/src/main/java/com/example/courseapp/data/remote/RetrofitProvider.kt`
2. Replace the `BASE` constant with your API URL
3. Ensure your API returns the same JSON structure

### Step 4: Build and Run

1. **Connect a device** or **start an emulator**
2. Click the **Run** button (▶) or press `Shift + F10`
3. Select your target device
4. Wait for the app to install and launch

### Step 5: Build APK (Optional)

To generate a debug APK:

```bash
./gradlew assembleDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

## 🎯 How It Works

### Data Flow

```
1. User Action → UI (Composable)
2. UI → ViewModel
3. ViewModel → Repository
4. Repository → Local DB / Remote API
5. Data flows back through the same chain
```

### Category Loading Strategy

1. **On First Launch**:
    - Fetch categories from API
    - Cache them in Room database
    - Display in category list

2. **On Subsequent Launches**:
    - Load cached categories immediately
    - Refresh from API in background
    - Update cache if successful

3. **When Offline**:
    - Use cached categories
    - Show categories from existing courses
    - No network errors displayed

4. **Custom Categories**:
    - User can add custom categories
    - Merged with API categories
    - Stored with courses automatically

### Score Calculation

When creating/editing a course:

- Title length is counted (including spaces)
- Multiplied by number of lessons
- Automatically updated on changes
- Displayed in preview before saving

## 📱 App Screens

### 1. Course List Screen

- Displays all courses in cards
- Search bar at the top
- Filter button for categories
- Floating action button to add courses
- Shows course title, description, category, lessons, and score
- Edit and delete buttons on each card

### 2. Add/Edit Course Screen

- Input fields for title and description
- Category selection (from API or custom)
- Number of lessons input
- Real-time score preview
- Form validation with error messages

### 3. Course Details Screen

- Large score display at top
- Full course information
- Category and lesson count cards
- Complete description
- Creation and update timestamps
- Edit and delete action buttons

## 🧪 Testing the App

### Test Scenario 1: Adding a Course

1. Launch the app
2. Tap "Add Course" floating button
3. Fill in course details
4. Select or create a category
5. Enter number of lessons
6. Observe score calculation
7. Tap "Create Course"
8. Verify course appears in list

### Test Scenario 2: Search & Filter

1. Create multiple courses
2. Use search bar to find courses
3. Tap filter button
4. Select a category
5. Verify filtered results
6. Clear filter to see all courses

### Test Scenario 3: Offline Mode

1. Create some courses while online
2. Enable airplane mode
3. Close and reopen app
4. Verify courses still visible
5. Create new courses offline
6. Edit existing courses
7. All changes should work normally

### Test Scenario 4: Categories

1. On first launch, categories load from API
2. Create a course with custom category
3. New category appears in filter list
4. Disable network
5. Categories still available
6. Custom categories persist

## 🔧 Troubleshooting

### Build Errors

**Issue**: Gradle sync fails

- **Solution**: Update Android Studio to latest version
- Run `./gradlew clean build`

**Issue**: Compilation errors with Compose

- **Solution**: Check Kotlin version compatibility
- Invalidate Caches: **File → Invalidate Caches / Restart**

### Runtime Errors

**Issue**: Database not working

- **Solution**: Uninstall app and reinstall (database will recreate)
- Check Room schema version

**Issue**: API not loading categories

- **Solution**: Check internet connection
- Verify API URL in `RetrofitProvider.kt`
- Check Logcat for network errors
- App will fallback to cached/local categories

**Issue**: App crashes on launch

- **Solution**: Check Logcat for error details
- Verify Hilt dependencies are properly configured
- Clean and rebuild project

## 📝 Development Notes

### Architecture Principles

- **Separation of Concerns**: Each layer has specific responsibilities
- **Dependency Inversion**: High-level modules independent of low-level modules
- **Single Responsibility**: Each class has one clear purpose
- **Unidirectional Data Flow**: Data flows in one direction

### Code Organization

- **Data Layer**: Manages data sources (API, Database)
- **Domain Layer**: Contains business models
- **Presentation Layer**: UI components and ViewModels
- **DI Layer**: Provides dependencies

### Best Practices Used

- Kotlin Coroutines for async operations
- StateFlow for reactive state management
- Sealed classes for UI states
- Repository pattern for data abstraction
- ViewModel for UI state preservation
- Room for reliable local storage

## 🔐 Permissions

The app requires the following permissions in `AndroidManifest.xml`:

- `INTERNET`: For fetching categories from API
- `ACCESS_NETWORK_STATE`: For checking network connectivity

## 📄 License

This project is created as a demonstration of Android development best practices.

## 🤝 Contributing

This is a demonstration project. For learning purposes, feel free to:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Support

For issues or questions:

1. Check the troubleshooting section above
2. Review the code comments in source files
3. Check Logcat for detailed error messages
4. Verify your development environment setup

## 🎓 Learning Resources

To understand this project better:

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Clean Architecture Principles](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

**Built with ❤️ using Jetpack Compose and Clean Architecture**

**Version**: 1.0.0  
**Last Updated**: December 2025
