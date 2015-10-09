package com.byoutline.kickmaterial.managers;

import android.databinding.ObservableArrayList;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.model.Category;

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
public class DataManager {

    public static ObservableArrayList<Category> getCategoriesList() {
        ObservableArrayList<Category> categories = new ObservableArrayList<>();

        categories.add(getCategoryAll());
        categories.add(new Category(1, R.string.art, R.color.art, R.drawable.art));
        categories.add(new Category(3, R.string.comics, R.color.comics, R.drawable.comics));
        categories.add(new Category(26, R.string.crafts, R.color.crafts, R.drawable.crafts));
        categories.add(new Category(6, R.string.dance, R.color.dance, R.drawable.dance));
        categories.add(new Category(7, R.string.design, R.color.design, R.drawable.design));
        categories.add(new Category(9, R.string.fashion, R.color.fashion, R.drawable.fashion));
        categories.add(new Category(11, R.string.film_video, R.color.film_video, R.drawable.film_video));
        categories.add(new Category(10, R.string.food, R.color.food, R.drawable.food));
        categories.add(new Category(12, R.string.games, R.color.games, R.drawable.games));
        categories.add(new Category(13, R.string.journalism, R.color.journalism, R.drawable.journalism));
        categories.add(new Category(14, R.string.music, R.color.music, R.drawable.music));
        categories.add(new Category(15, R.string.photography, R.color.photography, R.drawable.photography));
        categories.add(new Category(18, R.string.publishing, R.color.publishing, R.drawable.publishing));
        categories.add(new Category(16, R.string.technology, R.color.technology, R.drawable.technology));
        categories.add(new Category(17, R.string.theater, R.color.theater, R.drawable.theater));

        return categories;
    }

    public static Category getCategoryAll() {
        return new Category(Category.ALL_CATEGORIES_ID, R.string.all_categories, R.color.green_primary, 0);
    }
}
