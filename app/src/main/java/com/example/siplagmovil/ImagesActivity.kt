package com.example.siplagmovil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.siplagmovil.ui.images.ImageGalleryAdapter
import com.example.siplagmovil.ui.images.ImageGalleryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageGalleryAdapter
    private val viewModel: ImageGalleryViewModel by viewModels() // ViewModel initialization simplified

    // Register the image picker result contract
    private val pickImageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Add the selected image URI to the ViewModel
            viewModel.addImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images3)

        // Initialize the RecyclerView and its adapter
        recyclerView = findViewById(R.id.rvImageGallery)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = ImageGalleryAdapter(this)
        recyclerView.adapter = adapter

        // Observe the image list from the ViewModel
        viewModel.imageList.observe(this, Observer { imageList ->
            imageList?.let {
                adapter.submitList(it)
            }
        })

        // Load the initial list of images
        viewModel.loadImages()

        // Set up the "Add" button to launch the image picker
        val btnAddImage: FloatingActionButton = findViewById(R.id.fabAddImage)
        btnAddImage.setOnClickListener {
            // Launch the gallery picker when the "Add" button is clicked
            pickImageContract.launch("image/*")
        }

        // Apply window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rvImageGallery)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
