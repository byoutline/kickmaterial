package com.byoutline.kickmaterial.features.projectlist

import android.databinding.BindingAdapter
import android.databinding.ViewDataBinding
import android.view.View
import android.widget.ImageView
import com.byoutline.kickmaterial.BR
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ProjectGridItemBigBinding
import com.byoutline.kickmaterial.databinding.ProjectGridItemNormalBinding
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.features.search.BaseProjectItemViewModel
import com.byoutline.kickmaterial.transitions.AplaTransformation
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.rx.invokeOnFPause
import com.squareup.picasso.Picasso
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import javax.inject.Provider

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class ProjectListViewModel(showHeader: Boolean) {
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

    init {
        if (showHeader) items.insertItem(header)
        items.insertList(projects)
    }

    fun setItems(items: List<Project>)
            = projects.update(items.mapToViewModels())

    fun addItems(items: Collection<Project>)
            = projects.update((projects + items.mapToViewModels(projects.size)).toSet().toList())

    fun attachViewUntilPause(fragment: ProjectsListFragment) {
        this.projectClickListener = fragment
        fragment.invokeOnFPause { this.projectClickListener = null }
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
}

fun <T : BaseProjectItemViewModel> getProjectItemDiffObservableCallback() = object : DiffObservableList.Callback<T> {
    // Projects are immutable with unique IDs
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

class ProjectItemViewModel(project: Project, val type: Int, listenerProv: Provider<ProjectClickListener?>) : BaseProjectItemViewModel(project, listenerProv) {
    val percentProgress: Int = project.percentProgress.toInt()

    fun getTimeLeft() = project.getTimeLeft()

    fun onClick(view: View) = onClick()

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