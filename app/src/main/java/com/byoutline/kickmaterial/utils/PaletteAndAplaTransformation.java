package com.byoutline.kickmaterial.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Calculates palette in transformation step of picasso. One of way suggested
 * by Jake Wharton. <br />
 * This must be called as last picasso transformation, otherwise bitmap and calculated palette
 * will be discarded by GC.
 *
 * @see AplaTransformation
 * @see <a href="http://jakewharton.com/coercing-picasso-to-play-with-palette/">Jake Wharton blog</a>
 */
public final class PaletteAndAplaTransformation implements Transformation {
    private static final PaletteAndAplaTransformation INSTANCE = new PaletteAndAplaTransformation(new AplaTransformation());
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();
    private final AplaTransformation aplaTransformation;

    private PaletteAndAplaTransformation(AplaTransformation aplaTransformation) {
        this.aplaTransformation = aplaTransformation;
    }

    public static PaletteAndAplaTransformation instance() {
        return INSTANCE;
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        // Calculate palette from original bitmap.
        Palette palette = Palette.from(source).generate();
        // Add apla overlay to bitmap.
        Bitmap result = aplaTransformation.transform(source);
        // Cache palette with result as a key, since ImageView will have result bitmap set
        // (as opposed to source which at this point is recycled).
        CACHE.put(result, palette);
        return result;
    }

    @Override
    public String key() {
        return getClass().getSimpleName();
    }
}
