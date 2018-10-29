package com.byoutline.kickmaterial.features.search

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentSearchResultsBinding
import com.byoutline.kickmaterial.features.projectdetails.startProjectDetailsActivity
import com.byoutline.kickmaterial.features.projectlist.ProjectClickListener
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.kickmaterial.utils.KickMaterialFragment
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.EndlessScrollListener
import com.byoutline.kickmaterial.views.setEndlessScrollListener
import com.byoutline.secretsauce.activities.hideKeyboard
import com.byoutline.secretsauce.databinding.inflateAndSetVM
import com.byoutline.secretsauce.lifecycle.lazyViewModelWithAutoLifecycle
import org.jetbrains.anko.appcompat.v7.listeners.onClose
import org.jetbrains.anko.appcompat.v7.listeners.onQueryTextListener

class SearchListFragment : KickMaterialFragment(), ProjectClickListener, EndlessScrollListener {

    private lateinit var projectListRv: RecyclerView

    val viewModel by lazyViewModelWithAutoLifecycle(SearchViewModel::class)

    private var searchView: SearchView? = null
    private var restoredSearchQuery: CharSequence = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSearchResultsBinding = inflateAndSetVM(inflater, container, R.layout.fragment_search_results, viewModel)
        projectListRv = binding.searchRecyclerView

        setHasOptionsMenu(true)
        return projectListRv
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
    }

    override fun onResume() {
        super.onResume()
        restoreDefaultScreenLook()
        hostActivity?.setToolbarAlpha(1f)
    }

    private fun setUpAdapters() {
        projectListRv.setEndlessScrollListener(this)
        projectListRv.addItemDecoration(SearchListSeparator(KickMaterialApp.component.app))
    }

    private fun restoreDefaultScreenLook() {
        hostActivity?.showToolbar(true, false)
        LUtils.setStatusBarColor(activity!!, ContextCompat.getColor(context!!, R.color.status_bar_color))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(SI_KEY_SEARCH_QUERY, searchView!!.query)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        searchView = getSearchView(activity!!, menu).apply {
            isIconified = false
            onQueryTextListener {
                onQueryTextSubmit { searchTerm ->
                    viewModel.updateSearchTerm(searchTerm)
                    // On landscape entry field and soft keyboard may cover whole screen.
                    // Close keyboard when they press search, so they can see result.
                    activity?.hideKeyboard()
                    true
                }

                onQueryTextChange { searchTerm ->
                    viewModel.updateSearchTerm(searchTerm)
                    true
                }
            }
            setQuery(restoredSearchQuery, false)
            onClose {
                // Allow only clearing, do not allow closing.
                TextUtils.isEmpty(searchView!!.query)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun getFragmentToolbarName() = R.string.empty

    override fun showBackButtonInToolbar(): Boolean = false

    override fun projectClicked(project: Project, views: SharedViews) {
        activity?.startProjectDetailsActivity(project, views)
    }

    override val lastVisibleItemPosition: Int
        get() = (projectListRv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

    override fun loadMoreData() = viewModel.loadMoreData()

    override fun hasMoreDataAndNotLoading() = viewModel.hasMoreDataAndNotLoading()

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
