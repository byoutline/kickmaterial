package com.byoutline.kickmaterial.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.byoutline.kickmaterial.adapters.CategoryClickListener
import com.byoutline.kickmaterial.model.Category
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


/**
 * Allows to pass category to click listener.

 * @param view                  passed automatically by data binding
 * *
 * @param categoryClickListener listener to be informed about clicks
 * *
 * @param category              value passed to click listener
 */
@BindingAdapter("onClick", "category")
fun bindOnCategoryClicked(view: View, categoryClickListener: CategoryClickListener,
                          category: Category) {
    view.setOnClickListener { v -> categoryClickListener.categoryClicked(v, category) }
}
