package com.example.sakura.stickerdemo

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("imageResourceUrl")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view)
            .load(url)
            .into(view)
}