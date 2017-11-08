package com.byoutline.kickmaterial.features.selectcategory

import android.databinding.ObservableArrayList
import android.view.View
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.dagger.GlobalScope
import com.byoutline.kickmaterial.model.Category
import com.byoutline.secretsauce.rx.invokeOnAPause
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject


@GlobalScope
class CategoriesListViewModel @Inject
constructor() {
    val items: ObservableArrayList<Category> = DataManager.categoriesList
    val itemBinding: ItemBinding<Category>
    private var clickListener: CategoryClickListener? = null

    init {
        itemBinding = ItemBinding.of<Category>(BR.categoryItem, R.layout.category_list_item)
                .bindExtra(BR.categoryClickListener, object : CategoryClickListener {
                    override fun categoryClicked(view: View, category: Category) {
                        clickListener?.categoryClicked(view, category)
                    }
                })
    }

    fun setAllCategoriesBgColor(color: Int?) {
        items[0].setBgColor(color)
    }

    fun attachViewUntilPause(activity: CategoriesListActivity) {
        this.clickListener = activity
        activity.invokeOnAPause { this.clickListener = null }
    }
}