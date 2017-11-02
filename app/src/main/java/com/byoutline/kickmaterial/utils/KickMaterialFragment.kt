package com.byoutline.kickmaterial.utils

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.byoutline.secretsauce.activities.hideKeyboard
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

    override fun onDetach() {
        super.onDetach()
        hostActivity = StubHostActivity()
    }

    override fun onResume() {
        super.onResume()
        setActionbar()
    }

    override fun onDestroyView() {
        if (!isAdded) {
            activity?.hideKeyboard()
        }
        super.onDestroyView()
    }

    private fun setActionbar() {
        val baseActivity = activity as? KickMaterialBaseActivity ?: return
        if (!TextUtils.isEmpty(fragmentActionbarName)) {
            baseActivity.title = fragmentActionbarName
        }
        //            baseActivity.setDisplayHomeAsUpEnabled(showBackButtonInActionbar());
    }

    abstract val fragmentActionbarName: String

    abstract fun showBackButtonInActionbar(): Boolean


    interface HostActivity {
        fun enableActionBarAutoHide(listView: RecyclerView)

        fun showActionbar(show: Boolean, animate: Boolean)

        fun setToolbarAlpha(alpha: Float)

        fun setDisplayHomeAsUpEnabled(enabled: Boolean)
    }

    private class StubHostActivity : HostActivity {

        override fun enableActionBarAutoHide(listView: RecyclerView) {}

        override fun showActionbar(show: Boolean, animate: Boolean) {}

        override fun setToolbarAlpha(alpha: Float) {}

        override fun setDisplayHomeAsUpEnabled(enabled: Boolean) {}
    }
}
