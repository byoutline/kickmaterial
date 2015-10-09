package com.byoutline.kickmaterial.adapters;

import android.view.View;
import com.byoutline.kickmaterial.model.Category;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-25
 */
public interface CategoryClickListener {

    void categoryClicked(View view, Category category);
}
