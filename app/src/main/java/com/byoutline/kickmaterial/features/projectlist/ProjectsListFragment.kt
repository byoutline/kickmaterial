package com.byoutline.kickmaterial.features.projectlist

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentProjectsBinding
import com.byoutline.kickmaterial.features.projectdetails.startProjectDetailsActivity
import com.byoutline.kickmaterial.features.search.SearchListFragment
import com.byoutline.kickmaterial.features.selectcategory.ARG_CATEGORY
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListActivity
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.kickmaterial.utils.KickMaterialFragment
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.EndlessScrollListener
import com.byoutline.kickmaterial.views.setEndlessScrollListener
import com.byoutline.secretsauce.activities.showFragment
import com.byoutline.secretsauce.di.Injectable
import com.byoutline.secretsauce.di.inflateAndSetViewModel
import com.byoutline.secretsauce.di.lazyViewModelWithAutoLifecycle
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.support.v4.onRefresh
import timber.log.Timber


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectsListFragment : KickMaterialFragment(), Injectable, ProjectClickListener, FieldStateListener, EndlessScrollListener {

    private val viewModel: ProjectListViewModel by lazyViewModelWithAutoLifecycle(this as ProjectClickListener)
    lateinit var binding: FragmentProjectsBinding

    private lateinit var category: Category
    /**
     * Endless scroll variables *
     */
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var scrollListener: ProjectsListScrollListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflateAndSetViewModel(inflater, container, R.layout.fragment_projects, viewModel)

        hostActivity?.enableToolbarAutoHide(binding.projectRecyclerView)

        category = arguments!!.getParcelable(ARG_CATEGORY)
        viewModel.category = category
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
        setUpListeners()
        configureSwipeRefresh()
    }

    private fun configureSwipeRefresh() {
        val altColor = category.colorResId
        binding.swipeRefreshProjectsSrl.setColorSchemeResources(altColor, R.color.green_primary)
        binding.swipeRefreshProjectsSrl.onRefresh {
            // Throw away all loaded categories and start over.
            val pageToRefresh = 1
            viewModel.discoverField.refresh(DiscoverQuery.getDiscoverQuery(category, pageToRefresh))
        }
    }

    private fun setUpListeners() {
        binding.showCategoriesFab.onClick {
            CategoriesListActivity.launch(activity!!, category, binding.showCategoriesFab)
        }
        scrollListener = ProjectsListScrollListener(context!!, { hostActivity }, binding)
        binding.projectRecyclerView.addOnScrollListener(scrollListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(INSTANCE_STATE_SUMMARY_SCROLLED, scrollListener.summaryScrolled)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        scrollListener.summaryScrolled = savedInstanceState?.getFloat(INSTANCE_STATE_SUMMARY_SCROLLED) ?: 0f
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        restoreDefaultScreenLook()
        viewModel.discoverField.addStateListener(this)
        Timber.d("items will be refreshed ${viewModel.category}")
        viewModel.loadCurrentPage()
        binding.showCategoriesFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, category.colorResId))
    }

    override fun onPause() {
        viewModel.discoverField.removeStateListener(this)
        super.onPause()
    }

    private fun setUpAdapters() {
        layoutManager = GridLayoutManager(activity, 2)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int
                    = if (viewModel.items[position].type == ProjectItemViewModel.NORMAL_ITEM) 1 else 2
        }

        binding.projectRecyclerView.setEndlessScrollListener(this)
        binding.projectRecyclerView.layoutManager = layoutManager
    }

    private fun restoreDefaultScreenLook() {
        hostActivity?.showToolbar(true, false)
        LUtils.setStatusBarColor(activity!!, ContextCompat.getColor(context!!, R.color.status_bar_color))
    }

    override fun getFragmentToolbarName() = category.nameResId

    override fun showBackButtonInToolbar() = false

    override fun projectClicked(project: Project, views: SharedViews) {
        views.add(binding.showCategoriesFab)
        activity?.startProjectDetailsActivity(project, views)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        val searchView = SearchListFragment.getSearchView(activity!!, menu)
        searchView.isIconified = true
        searchView.setOnSearchClickListener { _ -> activity?.showFragment(SearchListFragment(), true) }
    }

    override fun fieldStateChanged(newState: FieldState) {
        binding.swipeRefreshProjectsSrl.isRefreshing = newState == FieldState.CURRENTLY_LOADING
    }

    override val lastVisibleItemPosition: Int
        get() = layoutManager.findLastVisibleItemPosition()

    override fun loadMoreData() = viewModel.loadMoreData()

    @Synchronized override fun hasMoreDataAndNotLoading() = viewModel.hasMoreDataAndNotLoading()

    companion object {
        const val PREFS_SHOW_HEADER = "PREFS_SHOW_HEADER"
        private const val INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED"

        fun newInstance(category: Category?): ProjectsListFragment {
            return ProjectsListFragment().apply {
                arguments = bundleOf(ARG_CATEGORY to category)
            }
        }
    }
}
