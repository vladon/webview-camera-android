package com.example.webviewcamera

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * CustomWebViewClient handles WebView page loading events.
 */
class CustomWebViewClient : WebViewClient() {
     
    companion object {
        private const val TAG = "CustomWebViewClient"
    }
    
    /**
     * Called when a page is about to be loaded.
     * Returns true to cancel the load, false to continue.
     */
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        // Allow loading of local asset files
        val url = request?.url?.toString() ?: return false
        
        // Allow loading from file://android_asset/
        if (url.startsWith("file://android_asset/")) {
            return false
        }
        
        // Block all other URLs for security
        return true
    }
    
    /**
     * Called when a page starts loading.
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        android.util.Log.d(TAG, "Page finished loading: $url")
    }
    
    /**
     * Called when a page starts loading.
     */
    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
        super.onPageStarted(view, url, favicon)
        android.util.Log.d(TAG, "Page started loading: $url")
    }
}
