# FIT5046_A4_Repo
Group 03 - Lab05- Assignment 4 Reposotory
# Food Donation App

A comprehensive Android application built with Kotlin and Jetpack Compose that connects food donors with food banks and helps reduce food waste while addressing food insecurity in communities.

## Features

### **Authentication & User Management**
- **Email & Password Registration/Login**
- **Google Sign-In Integration**
- **User Profile Management**
- **Account Settings & Security**
- **Account Deletion with Data Cleanup**

### **Find Food Banks**
- **Interactive Google Maps Integration**
- **10+ Static Food Bank Locations**
- **Real-time Status Updates (Open/Closed)**
- **Distance Calculation**
- **Search by Location**
- **Voice Search Support**
- **Filter Options**

### **Settings & Configuration**
- **Account Settings Management**
- **Security & Privacy Controls**
- **Notification Preferences**
- **Accessibility Features (Vision & Audio)**
- **Help & Support**
- **Terms & Policies**

### **Donation Reminders**
- **Background Reminder System**
- **WorkManager Integration**
- **Scheduled Daily Reminders**
- **Test Mode for Development**
- **Manual Trigger Options**

### **Data Management**
- **Hybrid Database Architecture (Room + Firebase)**
- **Offline-First Approach**
- **Data Synchronization**
- **Backup & Recovery**

##  Tech Stack

### **Frontend**
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - UI components and theming
- **Navigation Component** - Screen navigation

### **Backend & Database**
- **Firebase Authentication** - User authentication
- **Cloud Firestore** - Cloud database
- **Room Database** - Local database
- **Hybrid Repository Pattern** - Data management

### **Maps & Location**
- **Google Maps SDK** - Map integration
- **Google Places API** - Location services
- **Location Services** - GPS functionality

### **Additional Libraries**
- **WorkManager** - Background tasks
- **Coroutines** - Asynchronous programming
- **StateFlow** - Reactive state management
- **Dagger/Hilt** - Dependency injection

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24+)
- Google Services account
- Firebase project setup
- Google Maps API key

## Getting Started

### 1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/food-donation-app.git
cd food-donation-app
```

### 2. **Firebase Setup**
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the Firebase project
3. Download `google-services.json`
4. Place it in the `app/` directory

### 3. **Enable Firebase Services**
- **Authentication**: Enable Email/Password and Google Sign-In
- **Firestore Database**: Create database in production mode
- **Storage**: Enable if using file uploads

### 4. **Google Maps Setup**
1. Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
2. Add the API key to your `local.properties`:
```properties
MAPS_API_KEY=your_google_maps_api_key_here
```

### 5. **Build Configuration**
Add to your `build.gradle (Module: app)`:
```gradle
android {
    buildFeatures {
        buildConfig true
    }
}

dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.9.0'
    implementation 'androidx.activity:activity-compose:1.7.0'
    
    // Compose
    implementation platform('androidx.compose:compose-bom:2023.03.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.0.0')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.5.0'
    implementation 'androidx.room:room-ktx:2.5.0'
    kapt 'androidx.room:room-compiler:2.5.0'
    
    // Google Maps
    implementation 'com.google.maps.android:maps-compose:2.11.4'
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    
    // WorkManager
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
}
```

### 6. **Run the Application**
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Connect an Android device or start an emulator
4. Click "Run" or use `Ctrl+R`

## App Architecture

### **MVVM Pattern**
```
├── ui/
│   ├── screens/          # Compose screens
│   ├── components/       # Reusable UI components
│   └── theme/           # App theming
├── data/
│   ├── local/           # Room database
│   ├── remote/          # Firebase services
│   └── repository/      # Data repositories
├── domain/
│   ├── model/           # Data models
│   └── usecase/         # Business logic
└── di/                  # Dependency injection
```

### **Database Schema**

#### **Room Database (Local)**
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    // ... other fields
)
```

#### **Firestore Collections**
- `users/` - User profiles
- `donations/` - Donation records
- `scheduled_donations/` - Scheduled donations
- `reports/` - User reports

## Configuration

### **Firebase Configuration**
1. **Authentication Rules**: Configure in Firebase Console
2. **Firestore Security Rules**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### **Android Permissions**
Add to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## UI/UX Features

- **Material Design 3** implementation
- **Dark/Light theme** support
- **Responsive design** for different screen sizes
- **Accessibility features** (TalkBack, high contrast)
- **Smooth animations** and transitions
- **Intuitive navigation** with bottom navigation bar

## Security Features

- **Firebase Authentication** with secure token management
- **Input validation** and sanitization
- **Secure API communications**
- **Local data encryption** (Room database)
- **Account deletion** with complete data cleanup

## Performance Optimizations

- **Offline-first architecture** with Room database
- **Data synchronization** between local and cloud
- **Lazy loading** for large datasets
- **Image optimization** and caching
- **Background task optimization** with WorkManager

## Testing

### **Unit Tests**
```bash
./gradlew test
```

### **Instrumented Tests**
```bash
./gradlew connectedAndroidTest
```

### **UI Tests**
- **Compose UI Testing** framework
- **Espresso** for integration tests

## Deployment

### **Release Build**
```bash
./gradlew assembleRelease
```

### **App Signing**
1. Generate signed APK in Android Studio
2. Configure keystore and signing keys
3. Build release APK

## Monitoring & Analytics

- **Firebase Crashlytics** - Crash reporting
- **Firebase Analytics** - User behavior tracking
- **Performance Monitoring** - App performance metrics

## Contributing

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## Acknowledgments

- **Firebase** for backend services
- **Google Maps** for location services
- **Material Design** for UI components
- **Jetpack Compose** community for inspiration

## Support

For support, email support@fooddonationapp.com or create an issue in this repository.

## Version History

- **v1.0.0** - Initial release
  - Basic authentication and user management
  - Food bank finder with Google Maps
  - Settings and security features
  - Donation reminder system

---

**Made for reducing food waste and helping communities**
