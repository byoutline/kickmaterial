package com.byoutline.kickmaterial.features.projectlist

import android.databinding.BindingAdapter
import android.databinding.ViewDataBinding
import android.widget.ImageView
import com.byoutline.cachedfield.FieldState
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ProjectGridItemBigBinding
import com.byoutline.kickmaterial.databinding.ProjectGridItemNormalBinding
import com.byoutline.kickmaterial.features.search.BaseProjectItemViewModel
import com.byoutline.kickmaterial.model.*
import com.byoutline.kickmaterial.transitions.AplaTransformation
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.observablecachedfield.util.RetrofitHelper
import com.byoutline.observablecachedfield.util.registerChangeCallback
import com.byoutline.secretsauce.rx.invokeOnFPause
import com.squareup.picasso.Picasso
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import javax.inject.Provider

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class ProjectListViewModel(showHeader: Boolean,
                           val discoverField: ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery>) {
    private val projects = DiffObservableList<ProjectItemViewModel>(getProjectItemDiffObservableCallback(), false)
    val items = MergeObservableList<ProjectItemViewModel>()
    //    val itemBinding = ItemBinding.of<ProjectItemViewModel>(BR.categoryItem, R.layout.category_list_item)
    val itemBinding: ItemBinding<ProjectItemViewModel> = ItemBinding.of { itemBinding, _, item ->
        val layoutId = when (item.type) {
            ProjectItemViewModel.HEADER_ITEM -> R.layout.projects_list_header
            ProjectItemViewModel.BIG_ITEM -> R.layout.project_grid_item_big
            ProjectItemViewModel.NORMAL_ITEM -> R.layout.project_grid_item_normal
            else -> throw IllegalArgumentException("Unknown project item type: ${item.type}")
        }
        itemBinding.set(BR.viewModel, layoutId)
    }

    val adapter = ProjectListAdapter<ProjectItemViewModel>()
    private val header = ProjectItemViewModel(Project(), ProjectItemViewModel.HEADER_ITEM, Provider { null })
    private var projectClickListener: ProjectClickListener? = null
    private val projectClickListenerProv = Provider { projectClickListener }

    private var page = 1
    var category: Category? = null
        set(value) {
            if(value != field) {
                page = 1
                field = value
            }
        }
    private var lastAvailablePage = Integer.MAX_VALUE

    init {
        if (showHeader) items.insertItem(header)
        items.insertList(projects)
    }

    fun attachViewUntilPause(fragment: ProjectsListFragment) {
        this.projectClickListener = fragment
        val discoverFieldCallback = discoverField.registerChangeCallback(
                onNext = this::onDiscoverProjects,
                onError = this::onDiscoverProjectsFail
        )
        discoverField.observable().notifyChange()
        discoverField.observableError.notifyChange()
        fragment.invokeOnFPause {
            this.projectClickListener = null
            discoverField.observable().removeOnPropertyChangedCallback(discoverFieldCallback)
            discoverField.observableError.removeOnPropertyChangedCallback(discoverFieldCallback)
        }
    }

    private fun Collection<Project>.mapToViewModels(offset: Int = 0) = mapIndexed { index, project ->
        ProjectItemViewModel(project, getViewType(index + offset), projectClickListenerProv)
    }

    private fun getViewType(position: Int): Int {
        return if ((position + 2) % 5 == 4) {
            ProjectItemViewModel.BIG_ITEM
        } else {
            ProjectItemViewModel.NORMAL_ITEM
        }
    }

    private fun onDiscoverProjectsFail(exception: Exception, arg: DiscoverQuery) {
        if (isDiscoverFetchErrorCausedByLastPage(exception)) {
            val failedPage = arg.pageFromQuery
            if (failedPage != null) {
                page = failedPage - 1
                lastAvailablePage = page
            }
        }
    }

    private fun isDiscoverFetchErrorCausedByLastPage(exception: Exception): Boolean {
        if (exception is RetrofitHelper.ApiException) {
            return exception.errorResponse?.code() == 404
        }
        return false
    }

    private fun setItems(items: List<Project>)
            = projects.update(items.mapToViewModels())

    private fun addItems(items: Collection<Project>)
            = projects.update((projects + items.mapToViewModels(projects.size)).toSet().toList())

    private fun onDiscoverProjects(response: DiscoverResponse, arg: DiscoverQuery) {
        // ignore search result.
        if (arg.discoverType == DiscoverType.SEARCH) return
        if (response.projects == null || response.projects.isEmpty()) {
            lastAvailablePage = page
            return
        }
        lastAvailablePage = Integer.MAX_VALUE

        if (page == 1) {
            setItems(response.projects)
        } else {
            addItems(response.projects)
        }
    }

    private fun hasMore(): Boolean = page < lastAvailablePage

    fun loadMoreData() {
        page++
        loadCurrentPage()
    }

    fun loadCurrentPage() {
        val query = DiscoverQuery.getDiscoverQuery(category, page)
        discoverField.postValue(query)
    }

    fun hasMoreDataAndNotLoading() = discoverField.state != FieldState.CURRENTLY_LOADING && hasMore()
}


fun <T : BaseProjectItemViewModel> getProjectItemDiffObservableCallback() = object : DiffObservableList.Callback<T> {
    // Projects are immutable with unique IDs
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

class ProjectItemViewModel(project: Project, val type: Int, listenerProv: Provider<ProjectClickListener?>) : BaseProjectItemViewModel(project, listenerProv) {
    val percentProgress: Int = project.percentProgress.toInt()

    fun getTimeLeft() = project.getTimeLeft()

    fun onClick() {
        val listener = listenerProv.get() ?: return
        val views = when (type) {
            ProjectItemViewModel.BIG_ITEM -> {
                val bind = binding as? ProjectGridItemBigBinding ?: return
                with(bind) {
                    SharedViews(photoLayout!!.projectItemBigPhotoIv, projectItemBigTitleTv,
                            projectItemBigProgressSb, numberDetailsLayout!!.projectItemBigGatheredMoneyTv,
                            numberDetailsLayout.projectItemBigBackersTv, numberDetailsLayout.projectItemBigBackersLabelTv,
                            numberDetailsLayout.projectItemTimeLeftTypeTv, numberDetailsLayout.projectItemBigDaysLeft,
                            numberDetailsLayout.projectItemBigPledgedOfTv)
                }

            }
            ProjectItemViewModel.NORMAL_ITEM -> {
                val bind = binding as?  ProjectGridItemNormalBinding ?: return
                with(bind) {
                    SharedViews(photoLayout!!.projectItemBigPhotoIv, projectItemBigTitleTv,
                            projectItemBigProgressSb)
                }
            }
            else -> null
        } ?: return

        listener.projectClicked(project, views)
    }

    companion object {
        const val BIG_ITEM = 0
        const val NORMAL_ITEM = 1
        const val HEADER_ITEM = 2
    }
}

const val IMAGE_RATIO = (4 / 3).toDouble()


@BindingAdapter("projectImg")
fun setProjectImage(view: ImageView, projectVM: ProjectItemViewModel?) {
    if (projectVM == null) return
    val ctx = view.context
    val picasso = Picasso.with(ctx)
    val picassoBuilder = if (projectVM.type == ProjectItemViewModel.BIG_ITEM) {
        val bigItemHeight: Int = ctx.resources.getDimensionPixelSize(R.dimen.project_item_big_height)
        val bigItemWidth: Int = (bigItemHeight * IMAGE_RATIO).toInt()
        picasso.load(projectVM.project.bigPhotoUrl)
                .resize(bigItemWidth, bigItemHeight)
                .placeholder(R.drawable.blank_project_wide)
    } else {
        val smallItemHeight: Int = ctx.resources.getDimensionPixelSize(R.dimen.project_item_big_photo_height)
        val smallItemWidth: Int = (smallItemHeight * IMAGE_RATIO).toInt()
        picasso.load(projectVM.project.photoUrl)
                .resize(smallItemWidth, smallItemHeight)
                .placeholder(R.drawable.blank_project_small)
    }
    picassoBuilder.onlyScaleDown()
            .transform(AplaTransformation())
            .centerCrop()
            .into(view)
}


class ProjectListAdapter<T : BaseProjectItemViewModel> : BindingRecyclerViewAdapter<T>() {
    override fun onBindBinding(binding: ViewDataBinding?, variableId: Int, layoutRes: Int, position: Int, item: T?) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        item?.binding = binding
    }
}

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-25
 */
interface ProjectClickListener {

    fun projectClicked(project: Project, views: SharedViews)
}