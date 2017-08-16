package com.byoutline.kickmaterial.model

import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.byoutline.kickmaterial.utils.ColorMixer
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-24
 */
@PaperParcel
class Category(val categoryId: Int, @StringRes val nameResId: Int,
               @ColorRes val colorResId: Int,
               @DrawableRes val drawableResId: Int,
               @ColorRes private var bgColor: Int = Color.WHITE) : PaperParcelable {

    fun getBgColor() = bgColor

    fun setBgColor(color: Int?) {
        if (color == null) {
            bgColor = Color.WHITE
        } else {
            bgColor = ColorMixer.mixTwoColors(color, Color.WHITE, 0.20f)
        }
    }

    companion object {
        const val ALL_CATEGORIES_ID = -1
        @JvmField val CREATOR = PaperParcelCategory.CREATOR
    }
}



