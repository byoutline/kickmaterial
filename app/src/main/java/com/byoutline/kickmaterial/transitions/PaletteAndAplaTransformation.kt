package com.byoutline.kickmaterial.transitions

import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import com.squareup.picasso.Transformation
import java.util.*

/**
 * Calculates palette in transformation step of picasso. One of way suggested
 * by Jake Wharton. <br></br>
 * This must be called as last picasso transformation, otherwise bitmap and calculated palette
 * will be discarded by GC.

 * @see AplaTransformation

 * @see [Jake Wharton blog](http://jakewharton.com/coercing-picasso-to-play-with-palette/)
 */
class PaletteAndAplaTransformation private constructor(private val aplaTransformation: AplaTransformation) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        // Calculate palette from original bitmap.
        val palette = Palette.from(source).generate()
        // Add apla overlay to bitmap.
        val result = aplaTransformation.transform(source)
        // Cache palette with result as a key, since ImageView will have result bitmap set
        // (as opposed to source which at this point is recycled).
        CACHE.put(result, palette)
        return result
    }

    override fun key(): String {
        return javaClass.simpleName
    }

    companion object {
        private val INSTANCE = PaletteAndAplaTransformation(AplaTransformation())
        private val CACHE = WeakHashMap<Bitmap, Palette>()

        fun instance(): PaletteAndAplaTransformation {
            return INSTANCE
        }

        fun getPalette(bitmap: Bitmap): Palette? {
            return CACHE[bitmap]
        }
    }
}
