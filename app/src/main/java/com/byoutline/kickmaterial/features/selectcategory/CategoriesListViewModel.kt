package com.byoutline.kickmaterial.features.selectcategory

import android.databinding.ObservableArrayList
import android.view.View
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.secretsauce.di.AttachableViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding

class CategoriesListViewModel(
        private val discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>
) : AttachableViewModel<CategoryClickListener>() {
    val items: ObservableArrayList<Category> = DataManager.categoriesList
    val itemBinding: ItemBinding<Category>

    init {
        itemBinding = ItemBinding.of<Category>(BR.categoryItem, R.layout.category_list_item)
                .bindExtra(BR.categoryClickListener, object : CategoryClickListener {
                    override fun categoryClicked(view: View, category: Category) {
                        this@CategoriesListViewModel.view?.categoryClicked(view, category)
                    }
                })
    }

    fun setAllCategoriesBgColor(color: Int?) {
        items[0].setBgColor(color)
    }

    fun preloadCategory(category: Category) {
        discoverField.postValue(DiscoverQuery.getDiscoverQuery(category, 1))
    }
}