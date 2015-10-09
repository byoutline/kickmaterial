package com.byoutline.kickmaterial.utils;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.byoutline.kickmaterial.adapters.CategoriesRecyclerViewAdapter;
import com.byoutline.kickmaterial.adapters.CategoryClickListener;
import com.byoutline.kickmaterial.model.Category;
import com.squareup.picasso.Picasso;
import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;
import me.tatarka.bindingcollectionadapter.factories.BindingRecyclerViewAdapterFactory;

/**
 * Class that binds methods to be used in layout xml files by data binding.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class BindingAdapters {

    @BindingAdapter({"bind:imageUrl", "bind:error"})
    public static void loadImage(ImageView view, String imageUrl, Drawable error) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .error(error)
                .into(view);
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }

    @BindingAdapter({"bind:colorFilterRes"})
    public static void setColorFilter(ImageView view, @ColorRes int colorResId) {
        int color = ContextCompat.getColor(view.getContext(), colorResId);
        view.setColorFilter(color);
    }


    /**
     * Allows to pass category to click listener.
     *
     * @param view                  passed automatically by data binding
     * @param categoryClickListener listener to be informed about clicks
     * @param category              value passed to click listener
     */
    @BindingAdapter({"bind:onClick", "bind:category"})
    public static void bindOnCategoryClicked(View view, final CategoryClickListener categoryClickListener,
                                             final Category category) {
        view.setOnClickListener(v -> categoryClickListener.categoryClicked(v, category));
    }

    /**
     * Allows to pass additional parameter (click listener)
     * to adapter created by {@link me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapters}
     *
     * @param clickListener click listener to be bound to all items.
     * @return
     */
    @BindingConversion
    public static BindingRecyclerViewAdapterFactory toRecyclerViewAdapterFactory(final CategoryClickListener clickListener) {
        return new BindingRecyclerViewAdapterFactory() {
            public <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg) {
                return new CategoriesRecyclerViewAdapter<>(arg, clickListener);
            }
        };
    }
}
