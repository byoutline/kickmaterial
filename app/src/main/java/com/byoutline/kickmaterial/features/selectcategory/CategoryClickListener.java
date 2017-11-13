package com.byoutline.kickmaterial.features.selectcategory;

import android.view.View;

import com.byoutline.kickmaterial.model.Category;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-25
 */
public interface CategoryClickListener {
    void categoryClicked(@NotNull View view, @NotNull Category category);
}