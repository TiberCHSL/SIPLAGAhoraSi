package com.example.siplagmovil.ui.images

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siplagmovil.data.model.Image
import com.example.siplagmovil.data.model.ImageRepository
import com.example.siplagmovil.data.model.local.SharedPreferencesManager
import com.example.siplagmovil.data.network.ImageUploadService
import com.example.siplagmovil.data.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageGalleryViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData to hold image objects
    private val repository: ImageRepository
    private val _imageList = MutableLiveData<List<Image>>()
    val imageList: LiveData<List<Image>> get() = _imageList

    private val _uploadStatus = MutableLiveData<Boolean>() // To observe upload success/failure
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

    private val images: MutableList<Image> = mutableListOf()

    init {
        val sharedPreferences = application.getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        val apiService = RetrofitInstance.api // Use the RetrofitInstance to get ApiService
        val contentResolver = application.contentResolver
        repository = ImageRepository(sharedPreferences, apiService, contentResolver, application)
    }

    // Method to load images (this can be extended to get images from storage or an API)
    fun loadImages() {
        val imageUris = repository.loadImagesFromPreferences() // Returns List<Uri>
        val imageObjects = imageUris.map { uri -> Image(uri, "Image ${uri.hashCode()}") }
        images.clear() // Clear the old list to avoid duplicates
        images.addAll(imageObjects) // Add the new images
        _imageList.value = images.toList() //Se borra la lista anterior y se reemplaza por una nueva al cargar las imagenes
    }

    // Add multiple images
    fun addImages(imageUris: List<Uri>, token: String?) {
        viewModelScope.launch {
            if (token == null) {
                Log.e("ImageUpload", "Token is null. Cannot upload images.")
                return@launch
            }

            // Load existing images from SharedPreferences
            val existingImages = repository.loadImagesFromPreferences()

            // Combine existing and new images
            val allImages = existingImages + imageUris
            repository.saveImagesToPreferences(allImages)

            // Update LiveData with the new images
            loadImages()

            // Start the upload service for the new images
            startImageUploadService(imageUris, token)

        }
    }

    private fun startImageUploadService(imageUris: List<Uri>, token: String) {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, ImageUploadService::class.java).apply {
            putExtra("token", token)
            putParcelableArrayListExtra("imageUris", ArrayList(imageUris))
        }
        Log.d("ImageUpload", "Passing imageUris to service: $imageUris")
        context.startService(intent)
        Log.d("ImageUpload", "ImageUploadService started with ${imageUris.size} images.")
    }


    fun clearAllImages() {
        // Call repository to clear images
        repository.clearImagesFromPreferences()

        // Update the LiveData to reflect the change (empty list)
        _imageList.value = emptyList()
    }
    fun deleteImage(image: Image) {
        // Remove the image from repository (SharedPreferences)
        repository.deleteImage(image.uri)

        // Fetch the updated list from repository
        val updatedImages = repository.loadImagesFromPreferences()

        // Update LiveData and in-memory list
        _imageList.value = updatedImages.map { Image(it, "Image ${it.hashCode()}") }
    }

    // Method to update upload status
    fun setUploadStatus(isSuccess: Boolean) {
        _uploadStatus.value = isSuccess
    }

}

