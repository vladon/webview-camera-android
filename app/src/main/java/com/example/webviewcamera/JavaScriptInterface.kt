package com.example.webviewcamera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * JavaScriptInterface provides a bridge between JavaScript in WebView and native Android code.
 * It handles saving images to the device gallery.
 */
class JavaScriptInterface(private val context: Context) {
    
    companion object {
        private const val TAG = "JavaScriptInterface"
        private const val IMAGE_QUALITY = 90
    }
    
    /**
     * Save image to device gallery
     * @param base64Data Base64 encoded image data (data:image/jpeg;base64,...)
     */
    @JavascriptInterface
    fun saveImage(base64Data: String) {
        try {
            // Extract the Base64 data (remove data:image/jpeg;base64, prefix)
            val imageData = extractBase64Data(base64Data)
            
            // Decode Base64 to ByteArray
            val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
            
            // Decode ByteArray to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            
            if (bitmap != null) {
                val success = saveBitmapToGallery(bitmap)
                
                if (success) {
                    showToast(R.string.image_saved)
                    Log.d(TAG, "Image saved successfully")
                } else {
                    showToast(R.string.image_save_failed)
                    Log.e(TAG, "Failed to save image")
                }
            } else {
                showToast(R.string.image_save_failed)
                Log.e(TAG, "Failed to decode image from Base64")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            showToast(R.string.image_save_failed)
        }
    }
    
    /**
     * Extract Base64 data from data URL
     */
    private fun extractBase64Data(dataUrl: String): String {
        // Remove the data URL prefix (e.g., "data:image/jpeg;base64,")
        val commaIndex = dataUrl.indexOf(',')
        return if (commaIndex != -1) {
            dataUrl.substring(commaIndex + 1)
        } else {
            dataUrl
        }
    }
    
    /**
     * Save bitmap to device gallery
     */
    private fun saveBitmapToGallery(bitmap: Bitmap): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmapToGalleryApi29(bitmap)
        } else {
            saveBitmapToGalleryLegacy(bitmap)
        }
    }
    
    /**
     * Save bitmap to gallery using MediaStore API (Android 10+)
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveBitmapToGalleryApi29(bitmap: Bitmap): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WebViewCamera")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        return try {
            uri?.let {
                val outputStream: OutputStream = resolver.openOutputStream(it) ?: return false
                bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
                outputStream.close()
                
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image to gallery (API 29+)", e)
            false
        }
    }
    
    /**
     * Save bitmap to gallery using legacy method (Android 9 and below)
     */
    @Suppress("DEPRECATION")
    private fun saveBitmapToGalleryLegacy(bitmap: Bitmap): Boolean {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/WebViewCamera"
        
        val dir = File(imagesDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(dir, fileName)
        
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()
            
            // Notify the gallery about the new image
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image to gallery (legacy)", e)
            false
        }
    }
    
    /**
     * Show toast message
     */
    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show toast message with custom text
     */
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Log message for debugging
     */
    @JavascriptInterface
    fun log(message: String) {
        Log.d(TAG, "JS: $message")
    }
}
