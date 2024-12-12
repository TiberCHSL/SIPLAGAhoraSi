package com.example.siplagmovil.ui.images

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.siplagmovil.R
import com.example.siplagmovil.data.model.Image
import androidx.recyclerview.widget.DiffUtil

class ImageGalleryAdapter(
    private val context: Context,
    private val onDeleteClick: (Image) -> Unit
) : ListAdapter<Image, ImageGalleryAdapter.ImageViewHolder>(ImageDiffCallback()) {

    // Override onCreateViewHolder to inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        return ImageViewHolder(view)
    }

    // Override onBindViewHolder to bind the Image data to the views
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        Glide.with(context)
            .load(image.uri)  // Assuming Image has a `uri` property
            .into(holder.imageView)

        holder.tvImageName.text =
            image.name ?: "Image $position"  // Displaying the image name if available
        // Set up delete button click listener
        holder.btnDelete.setOnClickListener {
            onDeleteClick(image)
        }
    }

    // Return the size of the list
    override fun getItemCount(): Int = currentList.size

    // ViewHolder class to hold references to the views
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val tvImageName: TextView = itemView.findViewById(R.id.tvImageName)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    // DiffUtil callback to compare items and ensure efficient list updates
    class ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.uri == newItem.uri  // Assuming `uri` uniquely identifies an image
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}