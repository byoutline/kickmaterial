package com.byoutline.kickmaterial.managers;

import android.databinding.ObservableArrayList;
import android.support.annotation.Nullable;
import com.byoutline.kickmaterial.BR;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.kickmaterial.model.Category;
import me.tatarka.bindingcollectionadapter.ItemView;

import javax.inject.Inject;

@GlobalScope
public class CategoriesListViewModel {
    public final ObservableArrayList<Category> items = DataManager.getCategoriesList();
    public final ItemView itemView = ItemView.of(BR.categoryItem, R.layout.category_list_item);

    @Inject
    public CategoriesListViewModel() {
    }

    public void setAllCategoriesBgColor(@Nullable Integer color) {
        items.get(0).setBgColor(color);
    }
}