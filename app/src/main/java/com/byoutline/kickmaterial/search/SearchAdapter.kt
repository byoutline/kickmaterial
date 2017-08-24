package com.byoutline.kickmaterial.search

import android.databinding.ViewDataBinding
import android.view.View
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.SearchItemBinding
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.projectlist.ProjectClickListener
import com.byoutline.kickmaterial.projectlist.ProjectListAdapter
import com.byoutline.kickmaterial.projectlist.getProjectItemDiffObservableCallback
import com.byoutline.kickmaterial.transitions.SharedViews
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import javax.inject.Provider

/**
 * Displays search results
 */
class SearchViewModel {
    val adapter = ProjectListAdapter<SearchItemViewModel>()
    val items: DiffObservableList<SearchItemViewModel> = DiffObservableList(getProjectItemDiffObservableCallback(), false)
    val itemBinding: ItemBinding<SearchItemViewModel> = ItemBinding.of<SearchItemViewModel>(BR.viewModel, R.layout.search_item)
    private var projectClickListener: ProjectClickListener? = null
    private val projectClickListenerProv = Provider { projectClickListener }
    fun setItems(currentProjects: Collection<Project>) {
        items.update(currentProjects.mapToViewModels())
    }

    private fun Collection<Project>.mapToViewModels() = map { SearchItemViewModel(it, projectClickListenerProv) }
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

class SearchItemViewModel(project: Project, listenerProv: Provider<ProjectClickListener?>): BaseProjectItemViewModel(project, listenerProv) {
    fun onClick(view: View) {
        val listener = listenerProv.get() ?: return
        val bind = binding as SearchItemBinding
        val views = SharedViews(bind.searchItemPhotoIv, bind.searchItemTitleTv)
        listener.projectClicked(project, views)
    }
}