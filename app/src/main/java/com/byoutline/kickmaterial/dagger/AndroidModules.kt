package com.byoutline.kickmaterial.dagger

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.byoutline.kickmaterial.features.projectdetails.ProjectDetailsActivity
import com.byoutline.kickmaterial.features.projectlist.MainActivity
import com.byoutline.kickmaterial.features.projectlist.ProjectListViewModel
import com.byoutline.kickmaterial.features.projectlist.ProjectsListFragment
import com.byoutline.kickmaterial.features.rewardlist.RewardListViewModel
import com.byoutline.kickmaterial.features.rewardlist.RewardsListActivity
import com.byoutline.kickmaterial.features.search.SearchListFragment
import com.byoutline.kickmaterial.features.search.SearchViewModel
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListActivity
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListViewModel
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.secretsauce.di.ViewModelFactory
import com.byoutline.secretsauce.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = arrayOf(ProjectsListFragmentsModule::class))
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector abstract fun projectDetailsActivity(): ProjectDetailsActivity
    @ContributesAndroidInjector abstract fun rewardsListActivity(): RewardsListActivity
    @ContributesAndroidInjector abstract fun categoriesListActivity(): CategoriesListActivity
}

@Module
abstract class ProjectsListFragmentsModule {
    @ContributesAndroidInjector abstract fun projectsListFragment(): ProjectsListFragment
    @ContributesAndroidInjector abstract fun searchListFragment(): SearchListFragment
}

@Module
abstract class ViewModelMapModule {
    @Binds @Singleton
    abstract fun viewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Module
class ViewModelProvidersModule {

    @Provides @IntoMap
    @ViewModelKey(ProjectListViewModel::class)
    fun projectListViewModel(sharedPrefs: SharedPreferences,
                             discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>): ViewModel {
        // show header on first launch
        val showHeader = sharedPrefs.getBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, true)
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply()
        return ProjectListViewModel(showHeader, discoverField)
    }

    @Provides @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun searchViewModel(discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>): ViewModel
            = SearchViewModel(discoverField)

    @Provides @IntoMap
    @ViewModelKey(RewardListViewModel::class)
    fun rewardListViewModel(): ViewModel = RewardListViewModel()

    @Provides @IntoMap
    @ViewModelKey(CategoriesListViewModel::class)
    fun categoriesListViewModel(discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>): ViewModel
            = CategoriesListViewModel(discoverField)
}
