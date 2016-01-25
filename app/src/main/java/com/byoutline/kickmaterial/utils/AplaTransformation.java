package com.byoutline.kickmaterial.utils;

import android.graphics.*;
import com.squareup.picasso.Transformation;

/**
 * Adds apla to image.
 */
public class AplaTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        int colors[] = {Color.parseColor("#05000000"), Color.parseColor("#22000000"), Color.parseColor("#95000000")};
        LinearGradient gradient = new LinearGradient(0, 0, 0, height, colors, null, Shader.TileMode.CLAMP);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new ComposeShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP), gradient, PorterDuff.Mode.SRC_OVER));

        canvas.drawRect(new Rect(0, 0, width, height), paint);
        source.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return getClass().getSimpleName();
    }
}