package com.byoutline.kickmaterial.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Pair
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.byoutline.kickmaterial.R
import com.byoutline.secretsauce.utils.ViewUtils
import java.util.*


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
private const val HEADER_HIDE_ANIM_DURATION = 300

abstract class AutoHideToolbarActivity : AppCompatActivity(), KickMaterialFragment.HostActivity {
    private var toolbarAutoHideSensitivity = 0
    private var toolbarAutoHideMinY = 0
    private var toolbarAutoHideSignal = 0
    private var toolbarShown = true
    protected var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blockOrientationOnBuggedAndroidVersions()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toolbar = findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            toolbarTitle = findViewById(R.id.toolbar_title_tv)
            ViewCompat.setElevation(this, ViewUtils.convertDpToPixel(4.0f, this@AutoHideToolbarActivity))
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun blockOrientationOnBuggedAndroidVersions() {
        // On Android 5.0 rotating device in activity that was entered with
        // transition will cause crash during activity exit transition.
        // This bug was fixed in Android 5.1.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun enableToolbarAutoHide(listView: RecyclerView) {
        initToolbarAutoHide()
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val ITEMS_THRESHOLD = 1
            var lastFvi = 0

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = (recyclerView!!.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                onMainContentScrolled(if (firstVisibleItem <= ITEMS_THRESHOLD) 0 else Integer.MAX_VALUE,
                        when {
                            lastFvi - firstVisibleItem > 0 -> Integer.MIN_VALUE
                            lastFvi == firstVisibleItem -> 0
                            else -> Integer.MAX_VALUE
                        }
                )
                lastFvi = firstVisibleItem
            }
        })
    }

    /**
     * Initializes the Toolbar auto-hide (aka Quick Recall) effect.
     */
    private fun initToolbarAutoHide() {
        toolbarAutoHideMinY = resources.getDimensionPixelSize(R.dimen.action_bar_auto_hide_min_y)
        toolbarAutoHideSensitivity = resources.getDimensionPixelSize(R.dimen.action_bar_auto_hide_sensitivity)
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
        if (deltaY > toolbarAutoHideSensitivity) {
            deltaY = toolbarAutoHideSensitivity
        } else if (deltaY < -toolbarAutoHideSensitivity) {
            deltaY = -toolbarAutoHideSensitivity
        }

        if (Math.signum(deltaY.toFloat()) * Math.signum(toolbarAutoHideSignal.toFloat()) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            toolbarAutoHideSignal = deltaY
        } else {
            // add to accumulated signal
            toolbarAutoHideSignal += deltaY
        }

        val shouldShow = currentY < toolbarAutoHideMinY || toolbarAutoHideSignal <= -toolbarAutoHideSensitivity
        autoShowOrHideToolbar(shouldShow)
    }

    private fun autoShowOrHideToolbar(show: Boolean) {
        if (show == toolbarShown) {
            return
        }

        toolbarShown = show
        onToolbarAutoShowOrHide(show)
    }

    private fun onToolbarAutoShowOrHide(shown: Boolean) {
        val bar = toolbar ?: return

        val translationY = if (shown) 0f else (-bar.bottom).toFloat()
        val alpha = if (shown) 1f else 0f
        bar.animate()
                .translationY(translationY)
                .alpha(alpha)
                .setDuration(HEADER_HIDE_ANIM_DURATION.toLong()).interpolator = DecelerateInterpolator()
    }


    override fun showToolbar(show: Boolean, animate: Boolean) {
        if (animate) {
            autoShowOrHideToolbar(show)
        } else {
            ViewUtils.showView(toolbar, show)
        }
    }

    fun setToolbarText(@StringRes textId: Int) {
        toolbarTitle?.setText(textId)
    }
}

fun Activity.getSharedElementsBundle(vararg sharedViews: View): Bundle {
    return if (LUtils.hasL()) {
        getSharedElementsBundleL(*sharedViews)
    } else {
        Bundle()
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun Activity.getSharedElementsBundleL(vararg sharedViews: View?): Bundle {
    val decor = window.decorView

    val navBar = decor.findViewById<View>(android.R.id.navigationBarBackground)
    val toolbar = decor.findViewById<View>(R.id.toolbar)

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
    return ActivityOptions.makeSceneTransitionAnimation(this, *arr).toBundle()
}

fun FragmentActivity.setEnterSharedElementCallbackCompat(onSharedElementEnd: () -> Unit) {
    if (!LUtils.hasL()) return

    ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
        override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
            onSharedElementEnd()
        }
    })
}