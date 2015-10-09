package com.byoutline.kickmaterial.dagger;

import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.activities.CategoriesListActivity;
import com.byoutline.kickmaterial.activities.ProjectDetailsActivity;
import com.byoutline.kickmaterial.activities.RewardsListActivity;
import com.byoutline.kickmaterial.fragments.ProjectsListFragment;
import com.byoutline.kickmaterial.fragments.SearchListFragment;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.squareup.otto.Bus;
import dagger.Component;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.03.15.
 */
@GlobalScope
@Component(modules = GlobalModule.class)
public interface GlobalComponent {
    void inject(CategoriesListActivity fragment);

    void inject(SearchListFragment fragment);

    void inject(ProjectsListFragment fragment);

    void inject(ProjectDetailsActivity activity);

    void inject(RewardsListActivity rewardsListActivity);

    Bus getBus();

    KickMaterialApp getApp();

    AccessTokenProvider getAccessTokenProvider();
}
