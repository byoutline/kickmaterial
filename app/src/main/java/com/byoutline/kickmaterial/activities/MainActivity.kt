package com.byoutline.kickmaterial.activities

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.fragments.ProjectsListFragment
import com.byoutline.kickmaterial.managers.DataManager
import com.byoutline.kickmaterial.model.Category


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class MainActivity : KickMaterialBaseActivity() {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectViewsAndSetUpToolbar()
        //        setUpDrawer(true);
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, ProjectsListFragment.newInstance(DataManager.categoryAll))
                    .commit()
        }

        //        setExitSharedElementCallback(new SharedElementCallback() {
        //            @Override
        //            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        //                String fabName = getString(R.string.transition_fab);
        //                int fabIdx = sharedElementNames.indexOf(fabName);
        //                if (fabIdx >= 0) {
        //                    View fab = sharedElements.get(fabIdx);
        //                    fab.setTranslationZ(0);
        //                    fab.setTranslationX(0);
        //                }
        //            }
        //        });

    }

    override fun setToolbarAlpha(alpha: Float) {
        toolbar.background.alpha = (alpha * 255).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //        if (!navigationDrawerFragment.isDrawerOpen()) {
        menuInflater.inflate(R.menu.main, menu)
        return true
        //        }
        //        return super.onCreateOptionsMenu(menu);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val categorySelection = requestCode == CategoriesListActivity.DEFAULT_REQUEST_CODE
        if (categorySelection && resultCode == CategoriesListActivity.RESULT_CATEGORY_SELECTED) {
            val category = data?.getParcelableExtra<Category>(CategoriesListActivity.ARG_CATEGORY) ?: return
            showFragment(ProjectsListFragment.newInstance(category), true)
            setToolbarText(getString(category.nameResId))
        }
    }
}
