package com.byoutline.kickmaterial.utils

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Adds apla to image.
 */
class AplaTransformation : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val colors = intArrayOf(Color.parseColor("#05000000"), Color.parseColor("#22000000"), Color.parseColor("#95000000"))
        val gradient = LinearGradient(0f, 0f, 0f, height.toFloat(), colors, null, Shader.TileMode.CLAMP)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = ComposeShader(BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP), gradient, PorterDuff.Mode.SRC_OVER)

        canvas.drawRect(Rect(0, 0, width, height), paint)
        source.recycle()

        return bitmap
    }

    override fun key(): String {
        return javaClass.simpleName
    }
}