package com.example.siplagmovil.ui.images

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.siplagmovil.data.model.Image
import com.example.siplagmovil.data.model.ImageRepository

class ImageGalleryViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData to hold image objects
    private val repository: ImageRepository
    private val _imageList = MutableLiveData<List<Image>>()
    val imageList: LiveData<List<Image>> get() = _imageList

    private val images: MutableList<Image> = mutableListOf()

    init {
        val sharedPreferences = application.getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)
        repository = ImageRepository(sharedPreferences)
    }

    // Method to load images (this can be extended to get images from storage or an API)
    fun loadImages() {
        // For now, just initialize with some mock data if needed
        //val imageUris = listOf<Uri>(/* Some URIs here */)
        //val imageObjects = imageUris.map { Image(it, "Image ${it.hashCode()}") } // Create Image objects
        //images.addAll(imageObjects)
        //_imageList.value = images.toList()   // Se borra la lista anterior y se reemplaza por una nueva al cargar las imagenes
        val imageUris = repository.loadImagesFromPreferences() // Returns List<Uri>
        val imageObjects = imageUris.map { uri -> Image(uri, "Image ${uri.hashCode()}") }
        images.clear() // Clear the old list to avoid duplicates
        images.addAll(imageObjects) // Add the new images
        _imageList.value = images.toList() // Update LiveData
    }

    // Add multiple images
    fun addImages(imageUris: List<Uri>) {
        //val newImages = imageUris.map { uri -> Image(uri, "Image ${uri.hashCode()}") }
        //images.addAll(newImages)
        //_imageList.value = images.toList()

        // First, load the existing images from SharedPreferences
        val existingImages = repository.loadImagesFromPreferences()

        // Combine the existing images with the new ones
        val allImages = existingImages + imageUris

        // Save the combined list back to SharedPreferences
        repository.saveImagesToPreferences(allImages)
        loadImages()
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

