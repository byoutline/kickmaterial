package com.byoutline.kickmaterial.dagger

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.kickmaterial.features.projectdetails.ProjectDetailsActivity
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment
import com.byoutline.kickmaterial.features.rewardlist.RewardsListActivity
import com.byoutline.kickmaterial.features.search.SearchListFragment
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListActivity
import com.squareup.otto.Bus
import dagger.Component

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.03.15.
 */
@GlobalScope
@Component(modules = arrayOf(GlobalModule::class))
interface GlobalComponent {
    fun inject(fragment: CategoriesListActivity)
    fun inject(fragment: SearchListFragment)
    fun inject(fragment: ProjectsListFragment)
    fun inject(activity: ProjectDetailsActivity)
    fun inject(rewardsListActivity: RewardsListActivity)

    val bus: Bus
    val app: KickMaterialApp
    val discoverField: CachedFieldWithArg<DiscoverResponse, DiscoverQuery>
}
