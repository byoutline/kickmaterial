package com.byoutline.kickmaterial.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.activities.ProjectDetailsActivity
import com.byoutline.kickmaterial.adapters.ProjectClickListener
import com.byoutline.kickmaterial.adapters.SearchAdapter
import com.byoutline.kickmaterial.adapters.SharedViews
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedEvent
import com.byoutline.kickmaterial.model.*
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.EndlessRecyclerView
import com.byoutline.kickmaterial.views.SearchListSeparator
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import java.util.*
import javax.inject.Inject

class SearchListFragment : KickMaterialFragment(), ProjectClickListener, EndlessRecyclerView.EndlessScrollListener {

    lateinit var projectListRv: EndlessRecyclerView

    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var discoverField: CachedFieldWithArg<DiscoverResponse, DiscoverQuery>

    private var adapter: SearchAdapter? = null

    private var layoutManager: LinearLayoutManager? = null
    internal var page = DEFAULT_PAGE
    private var loading: Boolean = false
    private var hasMore = true
    private val currentProjects = ArrayList<Project>()
    private var currentSearchTerm: String? = null
    private var searchView: SearchView? = null
    private var restoredSearchQuery: CharSequence = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        projectListRv = inflater!!.inflate(R.layout.fragment_search_results, container, false) as EndlessRecyclerView

        KickMaterialApp.component.inject(this)
        setHasOptionsMenu(true)
        return projectListRv
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
    }

    override fun onResume() {
        super.onResume()
        restoreDefaultScreenLook()
        bus.register(this)
        hostActivity?.setToolbarAlpha(1f)
    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }

    private fun setUpAdapters() {
        projectListRv.setEndlessScrollListener(this)
        layoutManager = LinearLayoutManager(activity)
        projectListRv.layoutManager = layoutManager
        projectListRv.addItemDecoration(SearchListSeparator(activity.applicationContext))
        adapter = SearchAdapter(activity, this)
        projectListRv.adapter = adapter
    }

    private fun restoreDefaultScreenLook() {
        hostActivity?.showActionbar(true, false)
        LUtils.setStatusBarColor(activity, ContextCompat.getColor(context, R.color.status_bar_color))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putCharSequence(SI_KEY_SEARCH_QUERY, searchView!!.query)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            // We cannot restore search now, since menu has yet to be inflated.
            // Instead we store restored state to field to be restored later.
            restoredSearchQuery = savedInstanceState.getCharSequence(SI_KEY_SEARCH_QUERY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        searchView = getSearchView(activity, menu!!)

        searchView!!.isIconified = false
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchTerm: String): Boolean {
                updateSearchTerm(searchTerm)
                // On landscape entry field and soft keyboard may cover whole screen.
                // Close keyboard when they press search, so they can see result.
                hostActivity?.hideKeyboard()
                return true
            }

            override fun onQueryTextChange(searchTerm: String): Boolean {
                updateSearchTerm(searchTerm)
                return true
            }
        })
        searchView!!.setQuery(restoredSearchQuery, false)
        searchView!!.setOnCloseListener {
            // Allow only clearing, do not allow closing.
            TextUtils.isEmpty(searchView!!.query)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateSearchTerm(searchTerm: String) {
        currentSearchTerm = searchTerm
        page = DEFAULT_PAGE
        currentProjects.clear()

        if (!TextUtils.isEmpty(searchTerm)) {
            refreshSearchResult()
        } else {
            adapter!!.clear()
        }
    }

    private fun refreshSearchResult() {
        loading = true
        val query = DiscoverQuery.getDiscoverSearch(currentSearchTerm!!, null, page, SortTypes.MAGIC)
        discoverField.postValue(query)
    }

    override val fragmentActionbarName: String
        get() = " "

    override fun showBackButtonInActionbar(): Boolean {
        return false
    }

    override fun projectClicked(position: Int, views: SharedViews) {
        val project = adapter!!.getItem(position)!!
        ProjectDetailsActivity.launch(activity, project, *views.asArray())
    }

    @Subscribe
    fun onSearchResultFetched(event: DiscoverProjectsFetchedEvent) {
        loading = false

        if (event.response.projects?.isNotEmpty() ?: false) {
            hasMore = true
        }

        if (event.argValue.discoverType == DiscoverType.SEARCH) {
            currentProjects.addAll(event.response.projects!!)
            adapter!!.setItems(currentProjects)
        }
    }

    override val lastVisibleItemPosition: Int
        get() = layoutManager!!.findLastVisibleItemPosition()

    override fun loadMoreData() {
        page++
        refreshSearchResult()
    }

    override fun hasMoreDataAndNotLoading(): Boolean {
        return !loading && hasMore
    }

    companion object {
        const val DEFAULT_PAGE = 1
        private const val SI_KEY_SEARCH_QUERY = "SI_KEY_SEARCH_QUERY"

        fun getSearchView(context: Context, menu: Menu): SearchView {
            val item = menu.findItem(R.id.action_search)

            val searchView = item.actionView as SearchView
            searchView.queryHint = context.getString(R.string.search_hint)
            return searchView
        }
    }
}
