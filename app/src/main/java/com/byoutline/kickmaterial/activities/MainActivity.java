package com.byoutline.kickmaterial.activities;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.fragments.ProjectsListFragment;
import com.byoutline.kickmaterial.managers.DataManager;
import com.byoutline.kickmaterial.model.Category;
import org.parceler.Parcels;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class MainActivity extends KickMaterialBaseActivity {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injectViewsAndSetUpToolbar();
//        setUpDrawer(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ProjectsListFragment.newInstance(DataManager.getCategoryAll()))
                    .commit();
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

    @Override
    public void setToolbarAlpha(float alpha) {
        toolbar.getBackground().setAlpha((int) (alpha * 255));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!navigationDrawerFragment.isDrawerOpen()) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean categorySelection = requestCode == CategoriesListActivity.DEFAULT_REQUEST_CODE;
        if (categorySelection && resultCode == CategoriesListActivity.RESULT_CATEGORY_SELECTED) {
            Category category = Parcels.unwrap(data.getParcelableExtra(CategoriesListActivity.ARG_CATEGORY));
            showFragment(ProjectsListFragment.newInstance(category), true);
            setToolbarText(getString(category.nameResId));
        }
    }
}
