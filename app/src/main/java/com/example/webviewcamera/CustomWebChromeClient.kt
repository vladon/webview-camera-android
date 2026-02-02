package com.example.webviewcamera

import android.Manifest
import android.content.pm.PackageManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.content.ContextCompat

/**
 * CustomWebChromeClient handles WebView-specific UI events,
 * including permission requests for camera and microphone.
 */
class CustomWebChromeClient(
    private val context: android.content.Context,
    private val onPermissionRequest: (PermissionRequest, Boolean) -> Unit
) : WebChromeClient() {
    
    companion object {
        private const val TAG = "CustomWebChromeClient"
    }
    
    /**
     * Called when the WebView requests permission to access protected resources
     * such as camera, microphone, etc.
     */
    override fun onPermissionRequest(request: PermissionRequest) {
        android.util.Log.d(TAG, "Permission request: ${request.resources}")
        
        // Check if the requested resources include camera
        val hasCamera = request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
        val hasMicrophone = request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
        
        // Check if we have the necessary permissions
        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        // Grant permission if we have camera permission
        if (hasCamera && cameraPermission) {
            android.util.Log.d(TAG, "Granting camera permission")
            request.grant(request.resources)
            onPermissionRequest(request, true)
        } else {
            android.util.Log.d(TAG, "Denying permission request")
            request.deny()
            onPermissionRequest(request, false)
        }
    }
    
    /**
     * Called when the permission request is cancelled.
     */
    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        super.onPermissionRequestCanceled(request)
        android.util.Log.d(TAG, "Permission request canceled")
    }
    
    /**
     * Called to show a custom view (e.g., for fullscreen video).
     */
    override fun onShowCustomView(view: android.view.View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        android.util.Log.d(TAG, "Show custom view")
    }
    
    /**
     * Called to hide the custom view.
     */
    override fun onHideCustomView() {
        super.onHideCustomView()
        android.util.Log.d(TAG, "Hide custom view")
    }
    
    /**
     * Called when the page title changes.
     */
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        android.util.Log.d(TAG, "Page title: $title")
    }
    
    /**
     * Called when the progress bar needs to be updated.
     */
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        android.util.Log.d(TAG, "Progress: $newProgress%")
    }
}
