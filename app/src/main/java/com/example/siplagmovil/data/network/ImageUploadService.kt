package com.example.siplagmovil.data.network

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.example.siplagmovil.data.model.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageUploadService : Service() {
    private val repository: ImageRepository by lazy {
        val sharedPreferences = getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        val apiService = RetrofitInstance.api // Assuming you have a factory or DI for this
        val contentResolver = contentResolver // Built-in content resolver
        ImageRepository(sharedPreferences, apiService, contentResolver, applicationContext)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.e("ImageUploadService", "Received null Intent in onStartCommand")
            return START_NOT_STICKY
        }

        // Start a coroutine scope to handle retries
        CoroutineScope(Dispatchers.IO).launch {
            val pendingImages = repository.loadImagesFromPreferences()
            Log.d("ImageUploadService", "Loaded pending images: $pendingImages")
            val token = intent?.getStringExtra("token")
            Log.d("ImageUploadService", "Service started with token: $token and ${pendingImages?.size} images")
            token?.let {
                pendingImages.forEach { uri ->
                    uploadWithRetry(uri, token)
                }
            }
            stopSelf() // Stop service once retries are done
        }
        return START_NOT_STICKY
    }

    private suspend fun uploadWithRetry(uri: Uri, token: String) {
        while (true) { // Infinite retry loop
            try {
                val response = repository.uploadImage(uri, token)
                if (response.isSuccessful) {
                    Log.d("ImageUploadService", "Image uploaded: $uri")
                    repository.deleteImage(uri) // Delete from SharedPreferences

                    // Send a local broadcast to notify the Activity/ViewModel
                    val intent = Intent("com.example.siplagmovil.IMAGE_UPLOAD_COMPLETE")
                    intent.putExtra("upload_success", true)  // Indicate upload success
                    sendBroadcast(intent)

                    break
                } else {
                    Log.e("ImageUploadService", "Upload failed, retrying...")
                }
            } catch (e: Exception) {
                Log.e("ImageUploadService", "Error uploading: ${e.message}, retrying...")
            }
            delay(5000L) // Retry every 5 seconds
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}

