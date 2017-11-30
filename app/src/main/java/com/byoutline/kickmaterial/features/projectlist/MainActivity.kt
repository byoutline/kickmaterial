package com.byoutline.kickmaterial.features.projectlist

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.features.selectcategory.ARG_CATEGORY
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListActivity
import com.byoutline.kickmaterial.features.selectcategory.DataManager
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.utils.AutoHideToolbarActivity
import com.byoutline.secretsauce.activities.showFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class MainActivity : AutoHideToolbarActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingFragmentInjector

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, ProjectsListFragment.newInstance(DataManager.categoryAll))
                    .commit()
        }
    }

    override fun setToolbarAlpha(alpha: Float) {
        toolbar?.background?.alpha = (alpha * 255).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val categorySelection = requestCode == CategoriesListActivity.DEFAULT_REQUEST_CODE
        if (categorySelection && resultCode == CategoriesListActivity.RESULT_CATEGORY_SELECTED) {
            val category = data?.getParcelableExtra<Category>(ARG_CATEGORY) ?: return
            showFragment(ProjectsListFragment.newInstance(category), true)
            setToolbarText(category.nameResId)
        }
    }
}
