package com.example.siplagmovil.data.model

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.example.siplagmovil.data.network.ApiService
import com.example.siplagmovil.data.response.UploadResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException

class ImageRepository(
    private val sharedPreferences: SharedPreferences,
    private val apiService: ApiService, // Inject the API Service
    private val contentResolver: ContentResolver, // Needed to handle URIs
    private val context: Context
) {

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

    suspend fun uploadImage(uri: Uri, token: String): Response<UploadResponse> {
        val (file, originalFileName) = getFileFromUri(uri) ?: throw FileNotFoundException("File not found for URI: $uri")

        // Create a RequestBody and MultipartBody.Part using the original file name
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", originalFileName, requestBody)

        return apiService.uploadImage("Bearer $token", multipartBody)
    }


    // Helper function to convert URI to File
    private fun getFileFromUri(uri: Uri): Pair<File, String>? {
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        cursor.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val originalFileName = if (it.moveToFirst() && nameIndex != -1) it.getString(nameIndex) else "file.jpg"

            // Create a file in the cache directory with the original name
            val tempFile = File(context.cacheDir, originalFileName)
            contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            return tempFile to originalFileName // Return both the file and the original name
        }
    }








}