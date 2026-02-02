package com.example.webviewcamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

/**
 * MainActivity is the entry point of the application.
 * It sets up the WebView with camera capture functionality.
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MaterialTheme {
                WebViewCameraApp()
            }
        }
    }
}

/**
 * WebViewCameraApp is the main Composable that sets up the WebView with camera functionality.
 */
@Composable
fun WebViewCameraApp() {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasPermissions by remember { mutableStateOf(false) }
    var showPermissionDenied by remember { mutableStateOf(false) }
    
    // Check permissions on launch
    LaunchedEffect(Unit) {
        val permissionHandler = PermissionHandler(context)
        hasPermissions = permissionHandler.areAllPermissionsGranted()
        if (!hasPermissions) {
            showPermissionDenied = true
        }
    }
    
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasPermissions) {
                WebViewContent(
                    modifier = Modifier.fillMaxSize(),
                    onLoadFinished = { isLoading = false }
                )
            } else {
                PermissionDeniedScreen(
                    onRetry = {
                        val permissionHandler = PermissionHandler(context)
                        hasPermissions = permissionHandler.areAllPermissionsGranted()
                        if (!hasPermissions) {
                            Toast.makeText(
                                context,
                                R.string.permission_denied,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
            
            if (isLoading && hasPermissions) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * WebViewContent creates and configures the WebView with camera functionality.
 */
@Composable
fun WebViewContent(
    modifier: Modifier = Modifier,
    onLoadFinished: () -> Unit
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                // Configure WebView settings
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mediaPlaybackRequiresUserGesture = false
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }
                
                // Set WebViewClient
                webViewClient = CustomWebViewClient()
                
                // Set WebChromeClient for permission handling
                webChromeClient = CustomWebChromeClient(
                    context = ctx,
                    onPermissionRequest = { _, granted ->
                        if (!granted) {
                            Toast.makeText(
                                ctx,
                                R.string.camera_permission_message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
                
                // Add JavaScript interface
                addJavascriptInterface(JavaScriptInterface(ctx), "AndroidBridge")
                
                // Load local HTML file
                loadUrl("file:///android_asset/camera.html")
            }
        },
        update = { webView ->
            // Update WebView if needed
        }
    )
}

/**
 * PermissionDeniedScreen shows a message when permissions are not granted.
 */
@Composable
fun PermissionDeniedScreen(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera and storage permissions are required.\nPlease grant them in settings.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
