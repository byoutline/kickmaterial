package com.byoutline.kickmaterial.fragments

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.ibuscachedfield.util.RetrofitHelper
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.activities.CategoriesListActivity.Companion.ARG_CATEGORY
import com.byoutline.kickmaterial.activities.CategoriesListActivity.Companion.launch
import com.byoutline.kickmaterial.activities.ProjectDetailsActivity
import com.byoutline.kickmaterial.adapters.ProjectClickListener
import com.byoutline.kickmaterial.adapters.ProjectsAdapter
import com.byoutline.kickmaterial.adapters.SharedViews
import com.byoutline.kickmaterial.events.CategoriesFetchedEvent
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedErrorEvent
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedEvent
import com.byoutline.kickmaterial.managers.LoginManager
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.kickmaterial.model.DiscoverType
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.EndlessRecyclerView
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus
import com.byoutline.secretsauce.utils.ViewUtils
import com.software.shell.fab.ActionButton
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectsListFragment : KickMaterialFragment(), ProjectClickListener, FieldStateListener, EndlessRecyclerView.EndlessScrollListener {
    var summaryScrolled: Float = 0.toFloat()
    @JvmField @BindView(R.id.project_recycler_view)
    var projectListRv: EndlessRecyclerView? = null
    @JvmField @BindView(R.id.swipe_refresh_projects_srl)
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    @JvmField @BindView(R.id.bubbles_iv)
    var bubblesIv: ImageView? = null
    @JvmField @BindView(R.id.show_categories_fab)
    var showCategoriesFab: ActionButton? = null
    @JvmField @BindView(R.id.main_parent_rl)
    var mainParent: View? = null
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var discoverField: CachedFieldWithArg<DiscoverResponse, DiscoverQuery>
    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var rootView: View? = null
    private var adapter: ProjectsAdapter? = null
    private var actionbarScrollPoint: Float = 0.toFloat()
    private var maxScroll: Float = 0.toFloat()
    private var page = 1
    private var lastAvailablePage = Integer.MAX_VALUE
    private var category: Category? = null
    /**
     * Endless scroll variables *
     */
    private var layoutManager: GridLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_projects, container, false)
        KickMaterialApp.component.inject(this)
        ButterKnife.bind(this, rootView!!)
        hostActivity?.enableActionBarAutoHide(projectListRv!!)
        maxScroll = (2 * resources.getDimensionPixelSize(R.dimen.project_header_padding_top) + ViewUtils.dpToPx(48f, activity)).toFloat()
        actionbarScrollPoint = ViewUtils.dpToPx(24f, activity).toFloat()
        getArgs()
        setHasOptionsMenu(true)
        return rootView
    }

    private fun getArgs() {
        val args = arguments
        if (args != null && args.containsKey(ARG_CATEGORY)) {
            category = args.getParcelable<Category>(ARG_CATEGORY)
        } else {
            Timber.e("Category not passed")
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
        setUpListeners()
        configureSwipeRefresh()
    }

    fun configureSwipeRefresh() {
        val altColor = if (category == null) R.color.green_dark else category!!.colorResId
        swipeRefreshLayout!!.setColorSchemeResources(altColor, R.color.green_primary)
        swipeRefreshLayout!!.setOnRefreshListener {
            // Throw away all loaded categories and start over.
            val pageToRefresh = 1
            discoverField.refresh(DiscoverQuery.getDiscoverQuery(category, pageToRefresh))
        }
    }

    override fun setUpListeners() {
        super.setUpListeners()
        projectListRv!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > actionbarScrollPoint) {
                    hostActivity?.showActionbar(false, true)
                    if (!showCategoriesFab!!.isHidden) {
                        showCategoriesFab!!.hide()
                    }
                }

                if (dy < actionbarScrollPoint * -1) {
                    hostActivity?.showActionbar(true, true)

                    if (showCategoriesFab!!.isHidden) {
                        showCategoriesFab!!.show()
                    }
                }

                summaryScrolled += dy.toFloat()
                bubblesIv!!.translationY = -0.5f * summaryScrolled

                var alpha = summaryScrolled / maxScroll
                alpha = Math.min(1.0f, alpha)

                hostActivity?.setToolbarAlpha(alpha)

                //change background color on scroll
                val color = Math.max(BG_COLOR_MIN.toDouble(), BG_COLOR_MAX - summaryScrolled * 0.05).toInt()
                mainParent!!.setBackgroundColor(Color.argb(255, color, color, color))

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putFloat(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolled)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            summaryScrolled = savedInstanceState.getFloat(INSTANCE_STATE_SUMMARY_SCROLLED)
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        restoreDefaultScreenLook()
        bus.register(this)
        discoverField.addStateListener(this)
        loadCurrentPage()

        if (category != null) {
            showCategoriesFab!!.buttonColor = ContextCompat.getColor(context, category!!.colorResId)
        }
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
        /** NEW ADAPTER  */
        layoutManager = GridLayoutManager(activity, 2)

        val showHeader = sharedPreferences.getBoolean(PREFS_SHOW_HEADER, true)
        // TODO: decide when to hide it.
        sharedPreferences.edit().putBoolean(PREFS_SHOW_HEADER, false).apply()
        val itemViewTypeProvider = ProjectsAdapter.ItemViewTypeProvider(showHeader)

        layoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (itemViewTypeProvider.getViewType(position) == ProjectsAdapter.NORMAL_ITEM) {
                    return 1
                }
                return 2
            }
        }

        projectListRv!!.setEndlessScrollListener(this)
        projectListRv!!.layoutManager = layoutManager


        adapter = ProjectsAdapter(activity, this, showHeader, itemViewTypeProvider)
        projectListRv!!.adapter = adapter
    }

    private fun restoreDefaultScreenLook() {
        hostActivity?.showActionbar(true, false)
        LUtils.setStatusBarColor(activity, ContextCompat.getColor(context, R.color.status_bar_color))
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.show_categories_fab)
    fun showCategories() {
        //        projectListRv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.projects_list_hide_anim));
        launch(activity, category!!, showCategoriesFab!!)
    }

    override val fragmentActionbarName: String
        get() {
            if (category != null) {
                return getString(category!!.nameResId)
            } else {
                return "Projects"
            }
        }

    override fun showBackButtonInActionbar(): Boolean {
        return false
    }

    override fun projectClicked(position: Int, views: SharedViews) {
        val project = adapter!!.getItem(position)
        views.add(showCategoriesFab!!)
        ProjectDetailsActivity.launch(activity, project!!, *views.asArray())
    }

    private fun isDiscoverFetchErrorCausedByLastPage(event: DiscoverProjectsFetchedErrorEvent): Boolean {
        val exception = event.response
        if (exception is RetrofitHelper.ApiException) {
            val ex = exception
            if (ex.errorResponse != null && ex.errorResponse!!.code() == 404) {
                return true
            }
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
            if (event.response.projects != null && event.response.projects!!.size > 0) {
                lastAvailablePage = Integer.MAX_VALUE
            }

            val projects = ArrayList(event.response.projects!!)
            if (page == 1) {
                adapter!!.setItems(projects)
            } else {
                adapter!!.addItems(projects)
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
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout!!.isRefreshing = newState == FieldState.CURRENTLY_LOADING
            }
        }
    }

    override val lastVisibleItemPosition: Int
        get() = layoutManager!!.findLastVisibleItemPosition()

    override fun loadMoreData() {
        page++
        loadCurrentPage()
    }

    private fun hasMore(): Boolean {
        return page < lastAvailablePage
    }

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
