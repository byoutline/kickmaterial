package com.byoutline.kickmaterial.utils

import android.app.Activity
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
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
        setToolbarTitle()
    }

    override fun onDestroyView() {
        if (!isAdded) {
            activity?.hideKeyboard()
        }
        super.onDestroyView()
    }

    private fun setToolbarTitle() {
        val baseActivity = activity as? KickMaterialBaseActivity ?: return
        baseActivity.setToolbarText(getFragmentToolbarName())
    }

    @StringRes
    abstract fun getFragmentToolbarName(): Int

    abstract fun showBackButtonInToolbar(): Boolean


    interface HostActivity {
        fun enableToolbarAutoHide(listView: RecyclerView)

        fun showToolbar(show: Boolean, animate: Boolean)

        fun setToolbarAlpha(alpha: Float)
    }

    private class StubHostActivity : HostActivity {

        override fun enableToolbarAutoHide(listView: RecyclerView) {}

        override fun showToolbar(show: Boolean, animate: Boolean) {}

        override fun setToolbarAlpha(alpha: Float) {}
    }
}
