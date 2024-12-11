package com.example.siplagmovil.ui.images

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.siplagmovil.data.model.Image

class ImageGalleryViewModel : ViewModel() {

    // LiveData to hold image objects
    private val _imageList = MutableLiveData<List<Image>>()
    val imageList: LiveData<List<Image>> get() = _imageList

    private val images: MutableList<Image> = mutableListOf()

    init {
        loadImages()
    }

    // Method to load images (this can be extended to get images from storage or an API)
    fun loadImages() {
        // For now, just initialize with some mock data if needed
        val imageUris = listOf<Uri>(/* Some URIs here */)
        val imageObjects = imageUris.map { Image(it, "Image ${it.hashCode()}") } // Create Image objects
        images.addAll(imageObjects)
        _imageList.value = images.toList()   // Se borra la lista anterior y se reemplaza por una nueva al cargar las imagenes
    }


    //fun addImage(imageUri: Uri) {
        // Add the new image URI to the list and update the LiveData
        //images.add(imageUri)

        // Convert list of Uri to list of Image objects
        //val imageObjects = images.map { Image(it, "Image ${it.hashCode()}") }
        //_imageList.value = imageObjects
    //}
    // Add multiple images
    fun addImages(imageUris: List<Uri>) {
        val newImages = imageUris.map { uri -> Image(uri, "Image ${uri.hashCode()}") }
        images.addAll(newImages)
        _imageList.value = images.toList()
    }
}

