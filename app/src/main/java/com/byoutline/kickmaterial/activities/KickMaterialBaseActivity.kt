package com.byoutline.kickmaterial.activities

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.fragments.KickMaterialFragment
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.secretsauce.activities.BaseAppCompatActivity
import com.byoutline.secretsauce.fragments.MenuOption
import com.byoutline.secretsauce.fragments.NavigationDrawerFragment
import com.byoutline.secretsauce.utils.ViewUtils
import java.util.*

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
abstract class KickMaterialBaseActivity : BaseAppCompatActivity(), KickMaterialFragment.HostActivity, NavigationDrawerFragment.NavigationDrawerCallbacks {
    private var actionBarAutoHideSensitivity = 0
    private var actionBarAutoHideMinY = 0
    private var actionBarAutoHideSignal = 0
    private var actionBarShown = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blockOrientationOnBuggedAndroidVersions()
    }

    protected open fun shouldBlockOrientationOnBuggedAndroidVersions(): Boolean {
        return true
    }

    private fun blockOrientationOnBuggedAndroidVersions() {
        if (!shouldBlockOrientationOnBuggedAndroidVersions()) {
            return
        }
        // On Android 5.0 rotating device in activity that was entered with
        // transition will cause crash during activity exit transition.
        // This bug was fixed in Android 5.1.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    protected fun injectViewsAndSetUpToolbar() {
        injectViewsAndSetUpToolbar(R.id.toolbar, R.id.toolbar_title_tv)
        ViewCompat.setElevation(this.toolbar, ViewUtils.convertDpToPixel(4.0f, this))
    }

    override fun enableActionBarAutoHide(listView: RecyclerView) {
        initActionBarAutoHide()
        listView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            internal val ITEMS_THRESHOLD = 1
            internal var lastFvi = 0

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = (recyclerView!!.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                onMainContentScrolled(if (firstVisibleItem <= ITEMS_THRESHOLD) 0 else Integer.MAX_VALUE,
                        if (lastFvi - firstVisibleItem > 0)
                            Integer.MIN_VALUE
                        else if (lastFvi == firstVisibleItem) 0 else Integer.MAX_VALUE
                )
                lastFvi = firstVisibleItem
            }
        })
    }

    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private fun initActionBarAutoHide() {
        actionBarAutoHideMinY = resources.getDimensionPixelSize(R.dimen.action_bar_auto_hide_min_y)
        actionBarAutoHideSensitivity = resources.getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_sensivity)
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private fun onMainContentScrolled(currentY: Int, deltaY: Int) {
        var deltaY = deltaY
        if (deltaY > actionBarAutoHideSensitivity) {
            deltaY = actionBarAutoHideSensitivity
        } else if (deltaY < -actionBarAutoHideSensitivity) {
            deltaY = -actionBarAutoHideSensitivity
        }

        if (Math.signum(deltaY.toFloat()) * Math.signum(actionBarAutoHideSignal.toFloat()) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            actionBarAutoHideSignal = deltaY
        } else {
            // add to accumulated signal
            actionBarAutoHideSignal += deltaY
        }

        val shouldShow = currentY < actionBarAutoHideMinY || actionBarAutoHideSignal <= -actionBarAutoHideSensitivity
        autoShowOrHideActionBar(shouldShow)
    }

    protected fun autoShowOrHideActionBar(show: Boolean) {
        if (show == actionBarShown) {
            return
        }

        actionBarShown = show
        onActionBarAutoShowOrHide(show)
    }

    protected fun onActionBarAutoShowOrHide(shown: Boolean) {
        val view = toolbar

        if (shown) {
            view.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(HEADER_HIDE_ANIM_DURATION.toLong()).interpolator = DecelerateInterpolator()
        } else {
            view.animate()
                    .translationY((-view.bottom).toFloat())
                    .alpha(0f)
                    .setDuration(HEADER_HIDE_ANIM_DURATION.toLong()).interpolator = DecelerateInterpolator()
        }
    }


    override fun showActionbar(show: Boolean, animate: Boolean) {
        if (animate) {
            autoShowOrHideActionBar(show)
        } else {
            if (show) {
                supportActionBar!!.show()
            } else {
                supportActionBar!!.hide()
            }
        }
    }

    override fun setToolbarText(@StringRes textId: Int) {
        setToolbarText(getString(textId))
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onNavigationDrawerItemSelected(menuOption: MenuOption): Class<out android.support.v4.app.Fragment>? {
        // Currently there is no drawer
        return null
    }

    companion object {

        private const val HEADER_HIDE_ANIM_DURATION = 300

        fun getSharedElementsBundle(activity: Activity, vararg sharedViews: View): Bundle {
            val options: Bundle
            if (LUtils.hasL()) {
                options = getSharedElementsBundleL(activity, *sharedViews)
            } else {
                options = Bundle()
            }
            return options
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun getSharedElementsBundleL(activity: Activity, vararg sharedViews: View): Bundle {
            val options: Bundle
            val decor = activity.window.decorView

            val navBar = decor.findViewById(android.R.id.navigationBarBackground)
            val toolbar = decor.findViewById(R.id.toolbar)

            val sharedElements = sharedViews
                    .filterNotNull()
                    .mapTo(ArrayList<Pair<View, String>>()) { Pair(it, it.transitionName) }

            if (navBar != null) {
                sharedElements.add(Pair(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
            }
            if (toolbar != null) {
                sharedElements.add(Pair(toolbar, "toolbar"))
            }

            val arr = sharedElements.toTypedArray()
            options = ActivityOptions.makeSceneTransitionAnimation(activity, *arr).toBundle()
            return options
        }
    }
}
