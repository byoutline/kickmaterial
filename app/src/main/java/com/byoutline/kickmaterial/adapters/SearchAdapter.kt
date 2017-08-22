package com.byoutline.kickmaterial.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Project
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Displays search results
 */
class SearchAdapter(private val context: Context, private val projectClickListener: ProjectClickListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val dataset = ArrayList<Project>()

    fun getItem(position: Int): Project? {
        return dataset[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        // Create a new view by inflating the row item xml.
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        val holder = ViewHolder(v, projectClickListener)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = getItem(position)
        if (project != null) {
            ViewUtils.setText(holder.titleTv, project.projectName)
            ViewUtils.setText(holder.descTv, project.desc)

            Picasso.with(context).load(project.photoUrl).into(holder.photoIv)
        }

    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun setItems(items: List<Project>) {
        synchronized(dataset) {
            dataset.clear()
            dataset.addAll(items)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        synchronized(dataset) {
            dataset.clear()
            notifyDataSetChanged()
        }
    }


    // Create the ViewHolder class to keep references to your views
    class ViewHolder
    /**
     * @param v The container view which holds the elements from the row item xml
     */
    (v: View, var listener: ProjectClickListener?) : RecyclerView.ViewHolder(v), View.OnClickListener {

        internal var photoIv: ImageView
        internal var titleTv: TextView
        internal var descTv: TextView

        init {
            photoIv = v.findViewById(R.id.search_item_photo_iv)
            titleTv = v.findViewById(R.id.search_item_title_tv)
            descTv = v.findViewById(R.id.search_item_desc_tv)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (listener != null) {
                val views = SharedViews(photoIv, titleTv)
                listener!!.projectClicked(adapterPosition, views)
            }
        }
    }
}