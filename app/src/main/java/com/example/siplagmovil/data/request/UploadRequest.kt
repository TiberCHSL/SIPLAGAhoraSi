package com.example.siplagmovil.data.request

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class UploadRequest(
    val file: MultipartBody.Part,
)
