package com.example.siplagmovil.ui.images

import android.app.Application
import android.content.Context
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
import com.example.siplagmovil.data.network.RetrofitInstance
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

            // Upload each image immediately
            imageUris.forEach { uri ->
                try {
                    val response = repository.uploadImage(uri, token)
                    if (response.isSuccessful) {
                        Log.d("ImageUpload", "Image uploaded successfully: ${response.body()?.message}")
                        _uploadStatus.value = true
                    } else {
                        Log.e("ImageUpload", "Failed to upload image: ${response.errorBody()?.string()}")
                        _uploadStatus.value = false
                    }
                } catch (e: Exception) {
                    Log.e("ImageUpload", "Error uploading image: ${e.message}")
                    _uploadStatus.value = false
                }
            }
        }
    }




    fun clearAllImages() {
        // Call repository to clear images
        repository.clearImagesFromPreferences()

        // Update the LiveData to reflect the change (empty list)
        _imageList.value = emptyList()
    }
    fun deleteImage(image: Image) {
        // Get current images from repository
        val currentImages = repository.loadImagesFromPreferences()

        // Remove the image URI from the list
        val updatedImages = currentImages.filter { it != image.uri }

        // Save updated list back to repository
        repository.saveImagesToPreferences(updatedImages)

        // Update the in-memory list and LiveData
        images.remove(image)
        _imageList.value = images.toList()
    }




}

