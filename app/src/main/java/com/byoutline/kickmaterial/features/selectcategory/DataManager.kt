package com.byoutline.kickmaterial.features.selectcategory

import android.databinding.ObservableArrayList
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Category

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
object DataManager {

    val categoriesList: ObservableArrayList<Category>
        get() {
            val categories = ObservableArrayList<Category>()

            categories.add(categoryAll)
            categories.add(Category(1, R.string.art, R.color.art, R.drawable.art))
            categories.add(Category(3, R.string.comics, R.color.comics, R.drawable.comics))
            categories.add(Category(26, R.string.crafts, R.color.crafts, R.drawable.crafts))
            categories.add(Category(6, R.string.dance, R.color.dance, R.drawable.dance))
            categories.add(Category(7, R.string.design, R.color.design, R.drawable.design))
            categories.add(Category(9, R.string.fashion, R.color.fashion, R.drawable.fashion))
            categories.add(Category(11, R.string.film_video, R.color.film_video, R.drawable.film_video))
            categories.add(Category(10, R.string.food, R.color.food, R.drawable.food))
            categories.add(Category(12, R.string.games, R.color.games, R.drawable.games))
            categories.add(Category(13, R.string.journalism, R.color.journalism, R.drawable.journalism))
            categories.add(Category(14, R.string.music, R.color.music, R.drawable.music))
            categories.add(Category(15, R.string.photography, R.color.photography, R.drawable.photography))
            categories.add(Category(18, R.string.publishing, R.color.publishing, R.drawable.publishing))
            categories.add(Category(16, R.string.technology, R.color.technology, R.drawable.technology))
            categories.add(Category(17, R.string.theater, R.color.theater, R.drawable.theater))

            return categories
        }

    val categoryAll: Category
        get() = Category(Category.ALL_CATEGORIES_ID, R.string.all_categories, R.color.green_primary, 0)
}
