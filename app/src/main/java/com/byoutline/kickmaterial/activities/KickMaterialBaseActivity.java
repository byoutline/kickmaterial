package com.byoutline.kickmaterial.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import butterknife.ButterKnife;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.fragments.KickMaterialFragment;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.secretsauce.activities.BaseAppCompatActivity;
import com.byoutline.secretsauce.fragments.MenuOption;
import com.byoutline.secretsauce.fragments.NavigationDrawerFragment;
import com.byoutline.secretsauce.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public abstract class KickMaterialBaseActivity extends BaseAppCompatActivity implements KickMaterialFragment.HostActivity, NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private int actionBarAutoHideSensitivity = 0;
    private int actionBarAutoHideMinY = 0;
    private int actionBarAutoHideSignal = 0;
    private boolean actionBarShown = true;

    protected static Bundle getSharedElementsBundle(Activity activity, View... sharedViews) {
        final Bundle options;
        if (LUtils.hasL()) {
            options = getSharedElementsBundleL(activity, sharedViews);
        } else {
            options = new Bundle();
        }
        return options;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bundle getSharedElementsBundleL(Activity activity, View... sharedViews) {
        Bundle options;
        View decor = activity.getWindow().getDecorView();

        View navBar = decor.findViewById(android.R.id.navigationBarBackground);
        View toolbar = decor.findViewById(R.id.toolbar);

        List<Pair<View, String>> sharedElements = new ArrayList<>();
        for (View sharedElement : sharedViews) {
            if (sharedElement == null) {
                continue;
            }
            sharedElements.add(new Pair<>(sharedElement, sharedElement.getTransitionName()));
        }

        if (navBar != null) {
            sharedElements.add(new Pair<>(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }
        if (toolbar != null) {
            sharedElements.add(new Pair<>(toolbar, "toolbar"));
        }

        @SuppressWarnings("unchecked") Pair<View, String>[] arr = sharedElements.toArray(new Pair[sharedElements.size()]);
        options = ActivityOptions.makeSceneTransitionAnimation(activity, arr).toBundle();
        return options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockOrientationOnBuggedAndroidVersions();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected boolean shouldBlockOrientationOnBuggedAndroidVersions() {
        return true;
    }

    private void blockOrientationOnBuggedAndroidVersions() {
        if(!shouldBlockOrientationOnBuggedAndroidVersions()){
            return;
        }
        // On Android 5.0 rotating device in activity that was entered with
        // transition will cause crash during activity exit transition.
        // This bug was fixed in Android 5.1.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void injectViewsAndSetUpToolbar() {
        injectViewsAndSetUpToolbar(R.id.toolbar, R.id.toolbar_title_tv);
        ViewCompat.setElevation(this.toolbar, ViewUtils.convertDpToPixel(4.0F, this));
    }

    @Override
    public void enableActionBarAutoHide(final RecyclerView listView) {
        initActionBarAutoHide();
        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            final static int ITEMS_THRESHOLD = 1;
            int lastFvi = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        actionBarAutoHideMinY = getResources().getDimensionPixelSize(R.dimen.action_bar_auto_hide_min_y);
        actionBarAutoHideSensitivity = getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_sensivity);
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > actionBarAutoHideSensitivity) {
            deltaY = actionBarAutoHideSensitivity;
        } else if (deltaY < -actionBarAutoHideSensitivity) {
            deltaY = -actionBarAutoHideSensitivity;
        }

        if (Math.signum(deltaY) * Math.signum(actionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            actionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            actionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < actionBarAutoHideMinY ||
                (actionBarAutoHideSignal <= -actionBarAutoHideSensitivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == actionBarShown) {
            return;
        }

        actionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
        View view = toolbar;

        if (shown) {
            view.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(HEADER_HIDE_ANIM_DURATION)
                    .setInterpolator(new DecelerateInterpolator());
        } else {
            view.animate()
                    .translationY(-view.getBottom())
                    .alpha(0)
                    .setDuration(HEADER_HIDE_ANIM_DURATION)
                    .setInterpolator(new DecelerateInterpolator());
        }
    }


    @Override
    public void showActionbar(boolean show, boolean animate) {
        if (animate) {
            autoShowOrHideActionBar(show);
        } else {
            if (show) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }
    }

    public void setToolbarText(@StringRes int textId) {
        setToolbarText(getString(textId));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public Class<? extends android.support.v4.app.Fragment> onNavigationDrawerItemSelected(MenuOption menuOption) {
        // Currently there is no drawer
        return null;
    }
}
