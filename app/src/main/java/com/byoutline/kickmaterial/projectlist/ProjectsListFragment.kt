package com.byoutline.kickmaterial.projectlist

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.ibuscachedfield.util.RetrofitHelper
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentProjectsBinding
import com.byoutline.kickmaterial.model.*
import com.byoutline.kickmaterial.projectdetails.startProjectDetailsActivity
import com.byoutline.kickmaterial.search.SearchListFragment
import com.byoutline.kickmaterial.selectcategory.ARG_CATEGORY
import com.byoutline.kickmaterial.selectcategory.CategoriesListActivity
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.kickmaterial.utils.*
import com.byoutline.kickmaterial.views.EndlessRecyclerView
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectsListFragment : KickMaterialFragment(), ProjectClickListener, FieldStateListener, EndlessRecyclerView.EndlessScrollListener {
    private var summaryScrolled: Float = 0f

    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var discoverField: CachedFieldWithArg<DiscoverResponse, DiscoverQuery>
    @Inject
    lateinit var viewModel: ProjectListViewModel
    private var actionbarScrollPoint: Float = 0.toFloat()
    private var maxScroll: Float = 0.toFloat()
    private var page = 1
    private var lastAvailablePage = Integer.MAX_VALUE
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
        category = arguments.getParcelable(ARG_CATEGORY)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
        setUpListeners()
        configureSwipeRefresh()
    }

    fun configureSwipeRefresh() {
        val altColor = category.colorResId
        binding.swipeRefreshProjectsSrl.setColorSchemeResources(altColor, R.color.green_primary)
        binding.swipeRefreshProjectsSrl.setOnRefreshListener {
            // Throw away all loaded categories and start over.
            val pageToRefresh = 1
            discoverField.refresh(DiscoverQuery.getDiscoverQuery(category, pageToRefresh))
        }
    }

    override fun setUpListeners() {
        super.setUpListeners()
        binding.showCategoriesFab.setOnClickListener {
            CategoriesListActivity.launch(activity, category, binding.showCategoriesFab)
        }
        binding.projectRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > actionbarScrollPoint) {
                    hostActivity?.showActionbar(false, true)
                    if (!binding.showCategoriesFab.isHidden) {
                        binding.showCategoriesFab.hide()
                    }
                }

                if (dy < actionbarScrollPoint * -1) {
                    hostActivity?.showActionbar(true, true)

                    if (binding.showCategoriesFab.isHidden) {
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

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putFloat(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolled)
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
        bus.register(this)
        discoverField.addStateListener(this)
        loadCurrentPage()

        binding.showCategoriesFab.buttonColor = ContextCompat.getColor(context, category.colorResId)
    }

    override fun onPause() {
        discoverField.removeStateListener(this)
        bus.unregister(this)
        super.onPause()
    }

    private fun loadCurrentPage() {
        val query = DiscoverQuery.getDiscoverQuery(category, page)
        discoverField.postValue(query)
        //        loginManager.logIn(EmailAndPass.create("g774092@trbvm.com", "g774092@trbvm.com"));
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
        LUtils.setStatusBarColor(activity, ContextCompat.getColor(context, R.color.status_bar_color))
    }

    override val fragmentActionbarName: String
        get() = getString(category.nameResId)

    override fun showBackButtonInActionbar() = false

    override fun projectClicked(project: Project, views: SharedViews) {
        views.add(binding.showCategoriesFab)
        activity.startProjectDetailsActivity(project, views)
    }

    private fun isDiscoverFetchErrorCausedByLastPage(event: DiscoverProjectsFetchedErrorEvent): Boolean {
        val exception = event.response
        if (exception is RetrofitHelper.ApiException) {
            return exception.errorResponse?.code() == 404
        }
        return false
    }

    @Subscribe
    fun onCategoriesFetched(event: CategoriesFetchedEvent) {
        ViewUtils.showDebugToast(event.response.toString())
    }

    @Subscribe
    fun onDiscoverProjectsFail(event: DiscoverProjectsFetchedErrorEvent) {
        if (isDiscoverFetchErrorCausedByLastPage(event)) {
            val failedPage = event.argValue.pageFromQuery
            if (failedPage != null) {
                page = failedPage - 1
                lastAvailablePage = page
            }
        }
    }

    @Subscribe
    fun onDiscoverProjects(event: DiscoverProjectsFetchedEvent) {
        // ignore search result.
        if (event.argValue.discoverType != DiscoverType.SEARCH) {
            if (event.response.projects != null && event.response.projects!!.isNotEmpty()) {
                lastAvailablePage = Integer.MAX_VALUE
            }

            val projects = ArrayList(event.response.projects!!)
            if (page == 1) {
                viewModel.setItems(projects)
            } else {
                viewModel.addItems(projects)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val searchView = SearchListFragment.getSearchView(activity, menu!!)
        searchView.isIconified = true
        searchView.setOnSearchClickListener { _ -> hostActivity?.showFragment(SearchListFragment(), true) }
    }

    override fun fieldStateChanged(newState: FieldState) {
        PostFromAnyThreadBus.runInMainThread {
            binding.swipeRefreshProjectsSrl.isRefreshing = newState == FieldState.CURRENTLY_LOADING
        }
    }

    override val lastVisibleItemPosition: Int
        get() = layoutManager.findLastVisibleItemPosition()

    override fun loadMoreData() {
        page++
        loadCurrentPage()
    }

    private fun hasMore(): Boolean = page < lastAvailablePage

    @Synchronized override fun hasMoreDataAndNotLoading(): Boolean {
        return discoverField.state != FieldState.CURRENTLY_LOADING && hasMore()
    }

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
