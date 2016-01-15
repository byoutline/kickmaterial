package com.byoutline.kickmaterial.fragments;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.activities.ProjectDetailsActivity;
import com.byoutline.kickmaterial.adapters.ProjectClickListener;
import com.byoutline.kickmaterial.adapters.ProjectsAdapter;
import com.byoutline.kickmaterial.adapters.SharedViews;
import com.byoutline.kickmaterial.events.CategoriesFetchedEvent;
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedErrorEvent;
import com.byoutline.kickmaterial.events.DiscoverProjectsFetchedEvent;
import com.byoutline.kickmaterial.managers.LoginManager;
import com.byoutline.kickmaterial.model.*;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.kickmaterial.views.EndlessRecyclerView;
import com.byoutline.observablecachedfield.RetrofitHelper;
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.software.shell.fab.ActionButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.parceler.Parcels;
import timber.log.Timber;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;

import static com.byoutline.kickmaterial.activities.CategoriesListActivity.ARG_CATEGORY;
import static com.byoutline.kickmaterial.activities.CategoriesListActivity.launch;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class ProjectsListFragment extends KickMaterialFragment implements ProjectClickListener, FieldStateListener, EndlessRecyclerView.EndlessScrollListener {
    public static final String PREFS_SHOW_HEADER = "PREFS_SHOW_HEADER";
    private static final int BG_COLOR_MAX = 255;
    private static final int BG_COLOR_MIN = 232;
    private static final String INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED";
    public float summaryScrolled;
    @Bind(R.id.project_recycler_view)
    EndlessRecyclerView projectListRv;
    @Bind(R.id.swipe_refresh_projects_srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.bubbles_iv)
    ImageView bubblesIv;
    @Bind(R.id.show_categories_fab)
    ActionButton showCategoriesFab;
    @Bind(R.id.main_parent_rl)
    View mainParent;
    @Inject
    Bus bus;
    @Inject
    CachedFieldWithArg<DiscoverResponse, DiscoverQuery> discoverField;
    @Inject
    LoginManager loginManager;
    @Inject
    SharedPreferences sharedPreferences;
    private View rootView;
    private ProjectsAdapter adapter;
    private float actionbarScrollPoint;
    private float maxScroll;
    private int page = 1;
    private int lastAvailablePage = Integer.MAX_VALUE;
    private Category category;
    /**
     * Endless scroll variables *
     */
    private GridLayoutManager layoutManager;

    public static ProjectsListFragment newInstance(@Nullable Category category) {
        ProjectsListFragment instance = new ProjectsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CATEGORY, Parcels.wrap(category));
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        KickMaterialApp.component.inject(this);
        ButterKnife.bind(this, rootView);
        hostActivity.enableActionBarAutoHide(projectListRv);
        maxScroll = 2 * getResources().getDimensionPixelSize(R.dimen.project_header_padding_top) + ViewUtils.dpToPx(48, getActivity());
        actionbarScrollPoint = ViewUtils.dpToPx(24, getActivity());
        getArgs();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void getArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_CATEGORY)) {
            category = Parcels.unwrap(args.getParcelable(ARG_CATEGORY));
        } else {
            Timber.e("Category not passed");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpAdapters();
        setUpListeners();
        configureSwipeRefresh();
    }

    public void configureSwipeRefresh() {
        int altColor = category == null ? R.color.green_dark : category.colorResId;
        swipeRefreshLayout.setColorSchemeResources(altColor, R.color.green_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Throw away all loaded categories and start over.
            int pageToRefresh = 1;
            discoverField.refresh(DiscoverQuery.getDiscoverQuery(category, pageToRefresh));
        });
    }

    @Override
    protected void setUpListeners() {
        super.setUpListeners();
        projectListRv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > actionbarScrollPoint) {
                    hostActivity.showActionbar(false, true);
                    if (!showCategoriesFab.isHidden()) {
                        showCategoriesFab.hide();
                    }
                }

                if (dy < actionbarScrollPoint * (-1)) {
                    hostActivity.showActionbar(true, true);

                    if (showCategoriesFab.isHidden()) {
                        showCategoriesFab.show();
                    }
                }

                summaryScrolled += dy;
                bubblesIv.setTranslationY(-0.5f * summaryScrolled);

                float alpha = summaryScrolled / maxScroll;
                alpha = Math.min(1.0f, alpha);

                hostActivity.setToolbarAlpha(alpha);

                //change background color on scroll
                int color = (int) Math.max(BG_COLOR_MIN, BG_COLOR_MAX - summaryScrolled * 0.05);
                mainParent.setBackgroundColor(Color.argb(255, color, color, color));

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolled);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            summaryScrolled = savedInstanceState.getFloat(INSTANCE_STATE_SUMMARY_SCROLLED);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreDefaultScreenLook();
        bus.register(this);
        discoverField.addStateListener(this);
        loadCurrentPage();

        if (category != null) {
            showCategoriesFab.setButtonColor(ContextCompat.getColor(getContext(), category.colorResId));
        }
    }

    @Override
    public void onPause() {
        discoverField.removeStateListener(this);
        bus.unregister(this);
        super.onPause();
    }

    private void loadCurrentPage() {
        final DiscoverQuery query = DiscoverQuery.getDiscoverQuery(category, page);
        discoverField.postValue(query);
//        loginManager.logIn(EmailAndPass.create("g774092@trbvm.com", "g774092@trbvm.com"));
    }


    private void setUpAdapters() {
        /** NEW ADAPTER **/
        layoutManager = new GridLayoutManager(getActivity(), 2);

        final boolean showHeader = sharedPreferences.getBoolean(PREFS_SHOW_HEADER, true);
        // TODO: decide when to hide it.
        sharedPreferences.edit().putBoolean(PREFS_SHOW_HEADER, false).apply();
        final ProjectsAdapter.ItemViewTypeProvider itemViewTypeProvider = new ProjectsAdapter.ItemViewTypeProvider(showHeader);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (itemViewTypeProvider.getViewType(position) == ProjectsAdapter.NORMAL_ITEM) {
                    return 1;
                }
                return 2;
            }
        });

        projectListRv.setEndlessScrollListener(this);
        projectListRv.setLayoutManager(layoutManager);


        adapter = new ProjectsAdapter(getActivity(), this, showHeader, itemViewTypeProvider);
        projectListRv.setAdapter(adapter);
    }

    private void restoreDefaultScreenLook() {
        hostActivity.showActionbar(true, false);
        LUtils.setStatusBarColor(getActivity(), ContextCompat.getColor(getContext(), R.color.status_bar_color));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.show_categories_fab)
    public void showCategories() {
//        projectListRv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.projects_list_hide_anim));
        launch(getActivity(), category, showCategoriesFab);
    }

    @Override
    public String getFragmentActionbarName() {
        if (category != null) {
            return getString(category.nameResId);
        } else {
            return "Projects";
        }
    }

    @Override
    public boolean showBackButtonInActionbar() {
        return false;
    }

    @Override
    public void projectClicked(int position, SharedViews views) {
        Project project = adapter.getItem(position);
        views.add(showCategoriesFab);
        ProjectDetailsActivity.launch(getActivity(), project, views.asArray());
    }

    private boolean isDiscoverFetchErrorCausedByLastPage(DiscoverProjectsFetchedErrorEvent event) {
        Exception exception = event.getResponse();
        if (exception instanceof RetrofitHelper.ApiException) {
            RetrofitHelper.ApiException ex = (RetrofitHelper.ApiException) exception;
            if (ex.errorResponse != null && ex.errorResponse.code() == 404) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onCategoriesFetched(CategoriesFetchedEvent event) {
        ViewUtils.showDebugToast(event.getResponse().toString());
    }

    @Subscribe
    public void onDiscoverProjectsFail(DiscoverProjectsFetchedErrorEvent event) {
        if (isDiscoverFetchErrorCausedByLastPage(event)) {
            Integer failedPage = event.getArgValue().getPageFromQuery();
            if (failedPage != null) {
                page = failedPage - 1;
                lastAvailablePage = page;
            }
        }
    }

    @Subscribe
    public void onDiscoverProjects(DiscoverProjectsFetchedEvent event) {
        // ignore search result.
        if (event.getArgValue().discoverType != DiscoverType.SEARCH) {
            if (event.getResponse().projects != null && event.getResponse().projects.size() > 0) {
                lastAvailablePage = Integer.MAX_VALUE;
            }

            List<Project> projects = event.getResponse().projects;
            if (page == 1) {
                adapter.setItems(projects);
            } else {
                adapter.addItems(projects);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SearchView searchView = SearchListFragment.getSearchView(getActivity(), menu);
        searchView.setIconified(true);
        searchView.setOnSearchClickListener(v -> hostActivity.showFragment(new SearchListFragment(), true));
    }

    @Override
    public void fieldStateChanged(FieldState newState) {
        PostFromAnyThreadBus.runInMainThread(() -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(newState == FieldState.CURRENTLY_LOADING);
            }
        });
    }

    @Override
    public int getLastVisibleItemPosition() {
        return layoutManager.findLastVisibleItemPosition();
    }

    @Override
    public void loadMoreData() {
        page++;
        loadCurrentPage();
    }

    private boolean hasMore() {
        return page < lastAvailablePage;
    }

    @Override
    public synchronized boolean hasMoreDataAndNotLoading() {
        return (!(discoverField.getState() == FieldState.CURRENTLY_LOADING) && hasMore());
    }
}
