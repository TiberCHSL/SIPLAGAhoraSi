package com.example.siplagmovil.data.model

import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class ImageRepository(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun saveImagesToPreferences(imageUris: List<Uri>) {
        val json = gson.toJson(imageUris.map { it.toString() })
        sharedPreferences.edit().putString("IMAGE_URIS", json).apply()
    }

    fun loadImagesFromPreferences(): List<Uri> {
        val json = sharedPreferences.getString("IMAGE_URIS", "[]") // Use empty list as default
        return try {
            val uriStrings: List<String> =
                gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
            uriStrings.map { Uri.parse(it) }
        } catch (e: JsonSyntaxException) {
            emptyList() // Return empty list if parsing fails
        }
    }

    fun clearImagesFromPreferences() {
        // Remove the "IMAGE_URIS" key from SharedPreferences to clear the images
        sharedPreferences.edit().remove("IMAGE_URIS").apply()
    }

    fun deleteImage(uriToDelete: Uri) {
        val currentImages = loadImagesFromPreferences().toMutableList()
        currentImages.removeIf { it == uriToDelete }
        saveImagesToPreferences(currentImages)
    }

}