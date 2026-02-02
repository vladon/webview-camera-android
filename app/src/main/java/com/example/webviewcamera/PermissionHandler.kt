package com.example.webviewcamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

/**
 * PermissionHandler handles runtime permission requests for camera and storage.
 */
class PermissionHandler(private val context: Context) {
    
    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if storage permission is granted
     * Uses READ_MEDIA_IMAGES for API 33+ and WRITE_EXTERNAL_STORAGE for older versions
     */
    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(): Boolean {
        return isCameraPermissionGranted() && isStoragePermissionGranted()
    }
    
    /**
     * Get the storage permission based on Android version
     */
    fun getStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }
    
    /**
     * Get all required permissions
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}

/**
 * Composable to handle permission requests
 */
@Composable
fun PermissionRequestHandler(
    onAllPermissionsGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    var permissionsGranted by remember { mutableStateOf(false) }
    
    val permissionHandler = remember { PermissionHandler(androidx.compose.ui.platform.LocalContext.current) }
    
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            permissionsGranted = true
            onAllPermissionsGranted()
        } else {
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        if (permissionHandler.areAllPermissionsGranted()) {
            permissionsGranted = true
            onAllPermissionsGranted()
        } else {
            multiplePermissionsLauncher.launch(permissionHandler.getRequiredPermissions())
        }
    }
}
