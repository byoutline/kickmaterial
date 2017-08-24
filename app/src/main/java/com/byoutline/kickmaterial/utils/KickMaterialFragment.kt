package com.byoutline.kickmaterial.utils

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.byoutline.secretsauce.utils.LogUtils
import com.trello.rxlifecycle2.components.support.RxFragment


abstract class KickMaterialFragment : RxFragment() {

    protected var hostActivity: HostActivity? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            hostActivity = activity as? HostActivity?
        } catch (e: ClassCastException) {
            throw IllegalStateException(activity!!.javaClass.simpleName
                    + " does not implement " + HostActivity::class.java.simpleName
                    + " interface")
        }

    }


    fun showBackButtonInActionbar(show: Boolean) {
        val baseActivity = activity as? KickMaterialBaseActivity
        baseActivity?.setDisplayHomeAsUpEnabled(show)
    }


    protected open fun setUpListeners() {
        //empty by default
    }

    override fun onDetach() {
        super.onDetach()
        hostActivity = StubHostActivity()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpValidators()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setActionbar()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        if (!isAdded) {
            hostActivity?.hideKeyboard()
        }
        super.onDestroyView()
    }

    val screenName: String
        get() = javaClass.simpleName

    private fun setActionbar() {
        val baseActivity = activity as? KickMaterialBaseActivity
        if (baseActivity != null) {
            if (!TextUtils.isEmpty(fragmentActionbarName)) {
                baseActivity.title = fragmentActionbarName
            }
            //            baseActivity.setDisplayHomeAsUpEnabled(showBackButtonInActionbar());
        }
    }

    fun setActionbarTitle(title: String) {
        val baseActivity = activity as? KickMaterialBaseActivity
        if (baseActivity != null) {
            if (!TextUtils.isEmpty(title)) {
                baseActivity.title = title
            }
        }
    }


    abstract val fragmentActionbarName: String

    abstract fun showBackButtonInActionbar(): Boolean

    protected fun setUpValidators() {

    }

    interface HostActivity : com.byoutline.secretsauce.activities.HostActivityV4 {
        fun enableActionBarAutoHide(listView: RecyclerView)

        fun showActionbar(show: Boolean, animate: Boolean)

        fun setToolbarAlpha(alpha: Float)

    }

    private class StubHostActivity : com.byoutline.secretsauce.activities.StubHostActivityV4(), HostActivity {

        override fun enableActionBarAutoHide(listView: RecyclerView) {}

        override fun showActionbar(show: Boolean, animate: Boolean) {

        }

        override fun setToolbarAlpha(alpha: Float) {

        }
    }

    fun fakeOnResume() {
        setActionbarTitle(fragmentActionbarName)
    }

    companion object {

        private val TAG = LogUtils.makeLogTag(KickMaterialFragment::class.java)
    }
}
