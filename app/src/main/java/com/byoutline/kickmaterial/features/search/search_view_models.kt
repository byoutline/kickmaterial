package com.byoutline.kickmaterial.features.search

import android.databinding.ViewDataBinding
import android.text.TextUtils
import android.view.View
import com.byoutline.cachedfield.FieldState
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.SearchItemBinding
import com.byoutline.kickmaterial.features.projectlist.ProjectClickListener
import com.byoutline.kickmaterial.features.projectlist.ProjectListAdapter
import com.byoutline.kickmaterial.features.projectlist.getProjectItemDiffObservableCallback
import com.byoutline.kickmaterial.model.*
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.observablecachedfield.util.registerChangeCallback
import com.byoutline.rx.invokeOnFPause
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import javax.inject.Inject
import javax.inject.Provider

/**
 * Displays search results
 */
class SearchViewModel
@Inject constructor(private val discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>) {

    val adapter = ProjectListAdapter<SearchItemViewModel>()
    val items: DiffObservableList<SearchItemViewModel> = DiffObservableList(getProjectItemDiffObservableCallback(), false)
    val itemBinding: ItemBinding<SearchItemViewModel> = ItemBinding.of<SearchItemViewModel>(BR.viewModel, R.layout.search_item)
    private var projectClickListener: ProjectClickListener? = null
    private val projectClickListenerProv = Provider { projectClickListener }

    private fun setItems(currentProjects: Collection<Project>) {
        items.update(currentProjects.mapToViewModels())
    }

    private fun Collection<Project>.mapToViewModels() = map { SearchItemViewModel(it, projectClickListenerProv) }


    fun attachViewUntilPause(fragment: LifecycleProvider<FragmentEvent>) {
        val discoverFieldCallback = discoverField.registerChangeCallback(
                onNext = this::onSearchResultFetched
        )
        fragment.invokeOnFPause {
            this.projectClickListener = null
            discoverField.observable().removeOnPropertyChangedCallback(discoverFieldCallback)
            discoverField.observableError.removeOnPropertyChangedCallback(discoverFieldCallback)
        }
    }

    private var page = SearchListFragment.DEFAULT_PAGE
    private val currentProjects = HashSet<Project>()
    private var currentSearchTerm: String? = null
    private var hasMore = true

    fun updateSearchTerm(searchTerm: String) {
        currentSearchTerm = searchTerm
        page = SearchListFragment.DEFAULT_PAGE
        currentProjects.clear()

        if (!TextUtils.isEmpty(searchTerm)) {
            refreshSearchResult()
        } else {
            items.update(emptyList())
        }
    }

    private fun refreshSearchResult() {
        val query = DiscoverQuery.getDiscoverSearch(currentSearchTerm!!, null, page, SortTypes.MAGIC)
        discoverField.postValue(query)
    }

    private fun onSearchResultFetched(response: DiscoverResponse, arg: DiscoverQuery) {
        if (arg.discoverType != DiscoverType.SEARCH) return

        hasMore = response.projects?.isNotEmpty() == true

        if (arg.discoverType == DiscoverType.SEARCH) {
            currentProjects.addAll(response.projects.orEmpty())
            setItems(currentProjects)
        }
    }

    fun hasMoreDataAndNotLoading() = discoverField.state != FieldState.CURRENTLY_LOADING && hasMore

    fun loadMoreData() {
        page++
        refreshSearchResult()
    }
}

open class BaseProjectItemViewModel(val project: Project, protected val listenerProv: Provider<ProjectClickListener?>) {
    var binding: ViewDataBinding? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as? BaseProjectItemViewModel ?: return false

        return project == other.project
    }

    override fun hashCode() = project.hashCode()
}

class SearchItemViewModel(project: Project, listenerProv: Provider<ProjectClickListener?>) : BaseProjectItemViewModel(project, listenerProv) {
    fun onClick(view: View) {
        val listener = listenerProv.get() ?: return
        val bind = binding as SearchItemBinding
        val views = SharedViews(bind.searchItemPhotoIv, bind.searchItemTitleTv)
        listener.projectClicked(project, views)
    }
}