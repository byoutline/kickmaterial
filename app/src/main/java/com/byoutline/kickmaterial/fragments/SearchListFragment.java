package com.byoutline.kickmaterial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.*;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.activities.ProjectDetailsActivity;
import com.byoutline.kickmaterial.adapters.ProjectClickListener;
import com.byoutline.kickmaterial.adapters.SearchAdapter;
import com.byoutline.kickmaterial.adapters.SharedViews;
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedEvent;
import com.byoutline.kickmaterial.model.*;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.kickmaterial.views.EndlessRecyclerView;
import com.byoutline.kickmaterial.views.SearchListSeparator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends KickMaterialFragment implements ProjectClickListener, EndlessRecyclerView.EndlessScrollListener {

    EndlessRecyclerView projectListRv;

    @Inject
    Bus bus;
    @Inject
    CachedFieldWithArg<DiscoverResponse, DiscoverQuery> discoverField;

    private SearchAdapter adapter;

    private LinearLayoutManager layoutManager;
    public static final int DEFAULT_PAGE = 1;
    int page = DEFAULT_PAGE;
    private boolean loading;
    private boolean hasMore = true;
    private List<Project> currentProjects = new ArrayList<>();
    private String currentSearchTerm;
    private SearchView searchView;
    private static final String SI_KEY_SEARCH_QUERY = "SI_KEY_SEARCH_QUERY";
    private CharSequence restoredSearchQuery = "";

    public static SearchView getSearchView(Context context, Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(context.getString(R.string.search_hint));
        return searchView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        projectListRv = (EndlessRecyclerView) inflater.inflate(R.layout.fragment_search_results, container, false);

        KickMaterialApp.component.inject(this);
        setHasOptionsMenu(true);
        return projectListRv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpAdapters();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreDefaultScreenLook();
        bus.register(this);
        hostActivity.setToolbarAlpha(1);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    private void setUpAdapters() {
        projectListRv.setEndlessScrollListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        projectListRv.setLayoutManager(layoutManager);
        projectListRv.addItemDecoration(new SearchListSeparator(getActivity().getApplicationContext()));
        adapter = new SearchAdapter(getActivity(), this);
        projectListRv.setAdapter(adapter);
    }

    private void restoreDefaultScreenLook() {
        hostActivity.showActionbar(true, false);
        LUtils.setStatusBarColor(getActivity(), ContextCompat.getColor(getContext(), R.color.status_bar_color));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(SI_KEY_SEARCH_QUERY, searchView.getQuery());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            // We cannot restore search now, since menu has yet to be inflated.
            // Instead we store restored state to field to be restored later.
            restoredSearchQuery = savedInstanceState.getCharSequence(SI_KEY_SEARCH_QUERY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        searchView = getSearchView(getActivity(), menu);

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchTerm) {
                updateSearchTerm(searchTerm);
                // On landscape entry field and soft keyboard may cover whole screen.
                // Close keyboard when they press search, so they can see result.
                hostActivity.hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchTerm) {
                updateSearchTerm(searchTerm);
                return true;
            }
        });
        searchView.setQuery(restoredSearchQuery, false);
        searchView.setOnCloseListener(() -> {
            // Allow only clearing, do not allow closing.
            return TextUtils.isEmpty(searchView.getQuery());
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateSearchTerm(String searchTerm) {
        currentSearchTerm = searchTerm;
        page = DEFAULT_PAGE;
        currentProjects.clear();

        if (!TextUtils.isEmpty(searchTerm)) {
            refreshSearchResult();
        } else {
            adapter.clear();
        }
    }

    private void refreshSearchResult() {
        loading = true;
        DiscoverQuery query = DiscoverQuery.getDiscoverSearch(currentSearchTerm, null, page, SortTypes.MAGIC);
        discoverField.postValue(query);
    }

    @Override
    public String getFragmentActionbarName() {
        return " ";
    }

    @Override
    public boolean showBackButtonInActionbar() {
        return false;
    }

    @Override
    public void projectClicked(int position, SharedViews views) {
        Project project = adapter.getItem(position);
        ProjectDetailsActivity.launch(getActivity(), project, views.asArray());
    }

    @Subscribe
    public void onSearchResultFetched(DiscoverProjectsFetchedEvent event) {
        loading = false;

        if (event.getResponse().projects != null && event.getResponse().projects.size() > 0) {
            hasMore = true;
        }

        if (event.getArgValue().discoverType == DiscoverType.SEARCH) {
            currentProjects.addAll(event.getResponse().projects);
            adapter.setItems(currentProjects);
        }
    }

    @Override
    public int getLastVisibleItemPosition() {
        return layoutManager.findLastVisibleItemPosition();
    }

    @Override
    public void loadMoreData() {
        page++;
        refreshSearchResult();
    }

    @Override
    public boolean hasMoreDataAndNotLoading() {
        return !loading && hasMore;
    }
}
