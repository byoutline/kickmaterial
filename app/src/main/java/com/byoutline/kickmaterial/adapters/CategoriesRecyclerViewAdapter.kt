package com.byoutline.kickmaterial.adapters

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.byoutline.kickmaterial.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * [BindingRecyclerViewAdapter] that additionally requires [CategoryClickListener]
 */
class CategoriesRecyclerViewAdapter<T>(private val categoryClickListener: CategoryClickListener) : BindingRecyclerViewAdapter<T>() {
    override fun onCreateBinding(inflater: LayoutInflater?, layoutId: Int, viewGroup: ViewGroup?): ViewDataBinding {
        val binding = super.onCreateBinding(inflater, layoutId, viewGroup)
        binding.setVariable(BR.categoryClickListener, categoryClickListener)
        return binding
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        super.onBindViewHolder(holder, position, payloads)
    }
}
