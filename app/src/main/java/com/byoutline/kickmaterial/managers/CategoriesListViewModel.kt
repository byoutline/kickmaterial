package com.byoutline.kickmaterial.managers

import android.databinding.ObservableArrayList
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.dagger.GlobalScope
import com.byoutline.kickmaterial.model.Category
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@GlobalScope
class CategoriesListViewModel @Inject
constructor() {
    val items: ObservableArrayList<Category> = DataManager.categoriesList
    val itemBinding = ItemBinding.of<Category>(BR.categoryItem, R.layout.category_list_item)

    fun setAllCategoriesBgColor(color: Int?) {
        items[0].setBgColor(color)
    }
}