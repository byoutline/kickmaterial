package com.byoutline.kickmaterial.fragments;


import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.adapters.MenuAdapter;
import com.byoutline.secretsauce.fragments.MenuOption;
import com.byoutline.secretsauce.fragments.NavigationDrawerFragment;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragmentImpl extends NavigationDrawerFragment {


    @Override
    protected int getNavigationDrawerFragmentLayoutId() {
        return R.layout.fragment_navigation_drawer;
    }

    @Override
    protected int getNavigationDrawerListId() {
        return R.id.drawer_lv;
    }

    @Override
    protected ArrayAdapter<MenuOption> getListAdapter(Activity activity) {
        return new MenuAdapter(activity);
    }

    @Override
    protected int getAppNameId() {
        return R.string.app_name;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            //showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
