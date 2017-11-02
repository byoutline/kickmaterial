package com.byoutline.kickmaterial.features.projectlist

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.kickmaterial.KickMaterialApp
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
import com.byoutline.kickmaterial.views.EndlessRecyclerView
import com.byoutline.secretsauce.activities.showFragment
import com.byoutline.secretsauce.utils.ViewUtils
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectsListFragment : KickMaterialFragment(), ProjectClickListener, FieldStateListener, EndlessRecyclerView.EndlessScrollListener {
    private var summaryScrolled: Float = 0f

    @Inject
    lateinit var viewModel: ProjectListViewModel
    private var actionbarScrollPoint: Float = 0F
    private var maxScroll: Float = 0F

    private lateinit var category: Category
    private lateinit var binding: FragmentProjectsBinding
    /**
     * Endless scroll variables *
     */
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProjectsBinding.inflate(inflater, container, false)
        this.binding = binding
        KickMaterialApp.component.inject(this)
        binding.viewModel = viewModel

        hostActivity?.enableActionBarAutoHide(binding.projectRecyclerView)
        maxScroll = (2 * resources.getDimensionPixelSize(R.dimen.project_header_padding_top) + ViewUtils.dpToPx(48f, activity)).toFloat()
        actionbarScrollPoint = ViewUtils.dpToPx(24f, activity).toFloat()
        category = arguments!!.getParcelable(ARG_CATEGORY)
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
        binding.swipeRefreshProjectsSrl.setOnRefreshListener {
            // Throw away all loaded categories and start over.
            val pageToRefresh = 1
            viewModel.discoverField.refresh(DiscoverQuery.getDiscoverQuery(category, pageToRefresh))
        }
    }

    private fun setUpListeners() {
        binding.showCategoriesFab.setOnClickListener {
            CategoriesListActivity.launch(activity!!, category, binding.showCategoriesFab)
        }
        binding.projectRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > actionbarScrollPoint) {
                    hostActivity?.showActionbar(false, true)
                    if (binding.showCategoriesFab.visibility == View.VISIBLE) {
                        binding.showCategoriesFab.hide()
                    }
                }

                if (dy < actionbarScrollPoint * -1) {
                    hostActivity?.showActionbar(true, true)
                    if (binding.showCategoriesFab.visibility == View.GONE) {
                        binding.showCategoriesFab.show()
                    }
                }

                summaryScrolled += dy.toFloat()
                binding.bubblesIv.translationY = -0.5f * summaryScrolled

                var alpha = summaryScrolled / maxScroll
                alpha = Math.min(1.0f, alpha)

                hostActivity?.setToolbarAlpha(alpha)

                //change background color on scroll
                val color = Math.max(BG_COLOR_MIN.toDouble(), BG_COLOR_MAX - summaryScrolled * 0.05).toInt()
                binding.mainParentRl.setBackgroundColor(Color.argb(255, color, color, color))
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolled)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        summaryScrolled = savedInstanceState?.getFloat(INSTANCE_STATE_SUMMARY_SCROLLED) ?: 0f
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        restoreDefaultScreenLook()
        viewModel.attachViewUntilPause(this)
        viewModel.discoverField.addStateListener(this)
        viewModel.loadCurrentPage(category)
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
        hostActivity?.showActionbar(true, false)
        LUtils.setStatusBarColor(activity!!, ContextCompat.getColor(context!!, R.color.status_bar_color))
    }

    override val fragmentActionbarName: String
        get() = getString(category.nameResId)

    override fun showBackButtonInActionbar() = false

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

    override fun loadMoreData() {
        viewModel.loadMoreData(category)
    }

    @Synchronized override fun hasMoreDataAndNotLoading() = viewModel.hasMoreDataAndNotLoading()

    companion object {
        const val PREFS_SHOW_HEADER = "PREFS_SHOW_HEADER"
        private const val BG_COLOR_MAX = 255
        private const val BG_COLOR_MIN = 232
        private const val INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED"

        fun newInstance(category: Category?): ProjectsListFragment {
            val instance = ProjectsListFragment()
            val args = Bundle()
            args.putParcelable(ARG_CATEGORY, category)
            instance.arguments = args
            return instance
        }
    }
}
