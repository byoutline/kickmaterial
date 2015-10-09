package com.byoutline.kickmaterial.adapters;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import com.byoutline.kickmaterial.BR;
import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;

/**
 * {@link BindingRecyclerViewAdapter} that additionally requires {@link CategoryClickListener}
 *
 * @param <T>
 */
public class CategoriesRecyclerViewAdapter<T> extends BindingRecyclerViewAdapter<T> {
    private final CategoryClickListener categoryClickListener;

    public CategoriesRecyclerViewAdapter(@NonNull ItemViewArg<T> arg, CategoryClickListener categoryClickListener) {
        super(arg);
        this.categoryClickListener = categoryClickListener;
    }

    public void onBindBinding(ViewDataBinding binding, int bindingVariable, int layoutRes, int position, T item) {
        binding.setVariable(BR.categoryClickListener, categoryClickListener);
        super.onBindBinding(binding, bindingVariable, layoutRes, position, item);
    }
}
