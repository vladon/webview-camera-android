# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Build Commands
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew installDebug` - Install debug build to connected device/emulator
- `./gradlew test` - Run unit tests (no tests currently exist)
- `./gradlew connectedAndroidTest` - Run instrumented tests
- `./gradlew lint` - Run Android lint checks

## Critical Non-Obvious Patterns

### WebView Security
- WebView loads `file://android_asset/camera.html` - CustomWebViewClient blocks all external URLs for security
- Only `file://android_asset/` URLs are allowed; any other URL is blocked
- WebView settings require `mediaPlaybackRequiresUserGesture = false` for camera access

### JavaScript Bridge
- Methods in JavaScriptInterface MUST have `@JavascriptInterface` annotation to be callable from WebView
- Bridge handles Base64 image data with `data:image/jpeg;base64,` prefix that needs extraction

### Storage Permissions
- API 33+: Uses `READ_MEDIA_IMAGES` permission
- API < 33: Uses `WRITE_EXTERNAL_STORAGE` permission (with `maxSdkVersion="32"`)
- Images saved to `Pictures/WebViewCamera` directory via MediaStore API on API29+

### Edge-to-Edge Display
- Requires both `enableEdgeToEdge()` and `WindowCompat.setDecorFitsSystemWindows(window, false)` in onCreate

### Build Configuration
- Configuration cache is disabled (`org.gradle.configuration-cache=false`) due to plugin compatibility
- Kotlin code style is set to "official" in gradle.properties

### App Constraints
- Camera hardware is required (`android:required="true"`) - app won't install on devices without camera
- App is locked to portrait orientation (`android:screenOrientation="portrait"`)
