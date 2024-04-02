package com.example.androidphoto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidphoto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {



    companion object{
        private const val REQUEST_SELECT_FROM_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }
    private val imageList = ArrayList<Image>()
    private lateinit var adapter: ImageAdapter

    private var currentImageIndex = 0
    private val takePhotoList = ArrayList<Bitmap?>()


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter()
        binding.recyclerView.adapter = adapter




        binding.btnTakePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnSelectFromGallery.setOnClickListener {
            selectFromGallery()
        }

    }


    private fun dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun selectFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, REQUEST_SELECT_FROM_GALLERY)
        }
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
  if (requestCode == REQUEST_SELECT_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            if (data?.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    imageList.add(Image(uri))
                }
            } else if (data?.data != null) {
                val uri = data.data
                uri?.let { Image(it) }?.let { imageList.add(it) }
            }
            adapter.notifyDataSetChanged()
    }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

      val imageBitmap = data?.extras?.get("data") as Bitmap

      takePhotoList.add(imageBitmap)
            showNextImage()
            }
        }

    private fun showNextImage() {
        if (currentImageIndex < takePhotoList.size) {
            binding.imageView.setImageBitmap(takePhotoList[currentImageIndex])
            binding.imageView.visibility = ImageView.VISIBLE
            currentImageIndex++
        } else {
            binding.imageView.visibility = ImageView.GONE
        }
    }


    inner class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val image = imageList[position]
            holder.imageView.setImageURI(image.uri)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }
    }


}