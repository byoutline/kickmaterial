package com.byoutline.kickmaterial.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Class that binds methods to be used in layout xml files by data binding.

 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@BindingAdapter("imageUrl", "error")
fun loadImage(view: ImageView, imageUrl: String?, error: Drawable) {
    Picasso.with(view.context)
            .load(imageUrl)
            .error(error)
            .into(view)
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String) {
    Picasso.with(view.context)
            .load(imageUrl)
            .into(view)
}

@BindingAdapter("colorFilterRes")
fun setColorFilter(view: ImageView, @ColorRes colorResId: Int) {
    val color = ContextCompat.getColor(view.context, colorResId)
    view.setColorFilter(color)
}