# WebView Camera App

An Android application built with Kotlin and Jetpack Compose that displays a WebView with camera capture functionality. The camera is accessed via HTML5 getUserMedia API, and captured frames are saved to the device gallery.

## Features

- 📷 Camera capture using HTML5 getUserMedia API
- 💾 Save captured images directly to device gallery
- 🎨 Modern Material3 UI with Jetpack Compose
- 🔒 Runtime permission handling (Camera, Storage)
- 📱 Support for Android 7.0+ (API 24)
- 🌐 WebView with local HTML content

## Technology Stack

- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose
- **WebView**: Android WebView API
- **Camera Access**: HTML5 getUserMedia API (JavaScript)
- **Image Storage**: Android MediaStore API
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/webviewcamera/
│   │   │   ├── MainActivity.kt              # Main activity with WebView
│   │   │   ├── CustomWebViewClient.kt       # WebView page loading handler
│   │   │   ├── CustomWebChromeClient.kt     # WebView permission handler
│   │   │   ├── JavaScriptInterface.kt       # JS-Native bridge
│   │   │   └── PermissionHandler.kt         # Runtime permission manager
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── assets/
│   │       └── camera.html                  # Camera UI with JavaScript
├── build.gradle.kts
└── proguard-rules.pro
```

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 8 or higher
- Android SDK with API 34
- Gradle 8.2+

## Setup Instructions

### 1. Clone or Download the Project

```bash
git clone <repository-url>
cd WebViewCamera
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project directory and select it

### 3. Sync Gradle Files

Android Studio will automatically prompt you to sync Gradle files. If not:

1. Click "Sync Project with Gradle Files" in the toolbar
2. Wait for the sync to complete

### 4. Configure Device or Emulator

#### Using a Physical Device:
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect the device via USB
4. Ensure the device has a camera

#### Using an Emulator:
1. Create a new AVD (Android Virtual Device) in Android Studio
2. Choose a device with camera support (e.g., Pixel 5)
3. Enable camera support in AVD settings
4. Start the emulator

### 5. Run the Application

1. Click the "Run" button (green triangle) in the toolbar
2. Select your device/emulator from the list
3. Wait for the app to install and launch

## Permissions

The app requires the following permissions:

- **CAMERA**: Required to access the device camera
- **READ_MEDIA_IMAGES** (API 33+) or **WRITE_EXTERNAL_STORAGE** (API < 33): Required to save images to the gallery

These permissions are requested at runtime when the app is first launched.

## Usage

1. **Launch the App**: Open the WebView Camera app on your device
2. **Grant Permissions**: Allow camera and storage permissions when prompted
3. **View Camera Stream**: The camera feed will be displayed in the WebView
4. **Capture Image**: Tap the circular capture button to take a photo
5. **View Saved Image**: The image will be saved to your device gallery (Pictures/WebViewCamera folder)

## Architecture

### Native Android Layer (Kotlin + Jetpack Compose)

- **MainActivity.kt**: Entry point with WebView setup using Jetpack Compose
- **CustomWebViewClient.kt**: Handles page loading events and URL filtering
- **CustomWebChromeClient.kt**: Handles WebView permission requests (camera)
- **JavaScriptInterface.kt**: Bridge between JavaScript and native code for image saving
- **PermissionHandler.kt**: Manages runtime permission requests

### WebView Content Layer (HTML + JavaScript)

- **camera.html**: Local HTML file with camera UI
- **getUserMedia API**: Accesses device camera
- **Canvas API**: Captures video frames
- **Base64 Encoding**: Converts images for transmission to native code

### Data Flow

```
User → MainActivity → WebView → JavaScript (getUserMedia)
                                                    ↓
User → Capture Button → JavaScript (Canvas) → Base64 Data
                                                    ↓
JavaScriptInterface → MediaStore API → Device Gallery
```

## Troubleshooting

### Camera not working

1. Ensure camera permission is granted
2. Check if another app is using the camera
3. Try restarting the app
4. Verify the device has a working camera

### Images not saving to gallery

1. Ensure storage permission is granted
2. Check available storage space on the device
3. Look in the "Pictures/WebViewCamera" folder

### WebView not loading

1. Check the logs for errors in Android Studio
2. Ensure the `camera.html` file is in the `assets` folder
3. Verify JavaScript is enabled in WebView settings

### Permission dialog not showing

1. Ensure permissions are declared in AndroidManifest.xml
2. Check if permissions were permanently denied
3. Go to Settings > Apps > WebView Camera > Permissions to manually grant

## Building for Release

### 1. Configure Signing

1. Go to `Build > Generate Signed Bundle/APK`
2. Select "APK" and click Next
3. Create a new keystore or use an existing one
4. Fill in the keystore information
5. Select "release" build variant
6. Click Finish

### 2. Release Build

```bash
./gradlew assembleRelease
```

The release APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

## Development

### Adding New Features

1. **Camera Switching**: Modify `camera.html` to add front/rear camera toggle
2. **Image Preview**: Add a preview screen before saving
3. **Multiple Images**: Implement a gallery view within the app
4. **Image Editing**: Add basic editing capabilities (crop, rotate, filters)

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## License

This project is provided as-is for educational purposes.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For questions or issues, please open an issue on the project repository.
