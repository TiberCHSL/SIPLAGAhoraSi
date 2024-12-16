package com.example.siplagmovil

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siplagmovil.data.model.local.SharedPreferencesManager
import com.example.siplagmovil.ui.images.ImageGalleryAdapter
import com.example.siplagmovil.ui.images.ImageGalleryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageGalleryAdapter
    private val viewModel: ImageGalleryViewModel by viewModels()

    // Register receiver for the broadcast
    private val imageUploadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Get the result from the intent (whether upload was successful)
            val isSuccess = intent?.getBooleanExtra("upload_success", false) ?: false

            // Update the ViewModel with the upload result
            viewModel.setUploadStatus(isSuccess)

            // Refresh images after upload is completed
            viewModel.loadImages()
        }
    }
    private val pickMultipleImagesContract =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {
                val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
                val token = sharedPreferencesManager.getToken()

                if (token != null) {
                    // Trigger uploads and add images to the gallery
                    viewModel.addImages(it, token)
                } else {
                    Toast.makeText(this, "Authentication token not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images3)


        val filter = IntentFilter("com.example.siplagmovil.IMAGE_UPLOAD_COMPLETE")
        registerReceiver(imageUploadReceiver, filter)
        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.rvImageGallery)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = ImageGalleryAdapter(this) { image ->
            viewModel.deleteImage(image)
        }
        recyclerView.adapter = adapter

        // Observe image list
        viewModel.imageList.observe(this, Observer { imageList ->
            imageList?.let {
                adapter.submitList(it)
            }
        })

        // Observe upload status and provide feedback
        viewModel.uploadStatus.observe(this, Observer { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            } else if (isSuccess == false) {
                Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
            }
        })

        // Load initial list of images
        viewModel.loadImages()

        // "Add" button to launch image picker
        val btnAddImage: FloatingActionButton = findViewById(R.id.fabAddImage)
        btnAddImage.setOnClickListener {
            pickMultipleImagesContract.launch("image/*")
        }

        // "Clear" button to remove all images
        val clearButton: Button = findViewById(R.id.btnClear)
        clearButton.setOnClickListener {
            viewModel.clearAllImages()
        }

        // Edge-to-edge support for RecyclerView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rvImageGallery)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when activity is destroyed
        unregisterReceiver(imageUploadReceiver)
    }
}

