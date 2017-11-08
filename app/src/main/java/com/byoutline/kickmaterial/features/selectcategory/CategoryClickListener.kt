package com.byoutline.kickmaterial.features.selectcategory

import android.view.View
import com.byoutline.kickmaterial.model.Category

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-25
 */
interface CategoryClickListener {

    fun categoryClicked(view: View, category: Category)
}
