package com.byoutline.kickmaterial.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.utils.AplaTransformation
import com.byoutline.kickmaterial.utils.OrderedSet
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectsAdapter// Adapter's Constructor
(private val context: Context, private val projectClickListener: ProjectClickListener, private val showHeader: Boolean, private val itemViewTypeProvider: ItemViewTypeProvider) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {


    private val dataset = OrderedSet<Project>()
    private val smallItemHeight: Int = context.resources.getDimensionPixelSize(R.dimen.project_item_big_photo_height)
    private val smallItemWidth: Int = (smallItemHeight * IMAGE_RATIO).toInt()
    private val bigItemHeight: Int = context.resources.getDimensionPixelSize(R.dimen.project_item_big_height)
    private val bigItemWidth: Int = (bigItemHeight * IMAGE_RATIO).toInt()

    fun getItem(position: Int): Project? {
        if (showHeader) {
            dataset.get(position - 1)
        }
        return dataset.get(position)
    }

    // Create new views. This is invoked by the layout manager.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsAdapter.ViewHolder {
        // Create a new view by inflating the row item xml.
        var v: View? = null
        when (viewType) {
            BIG_ITEM -> v = LayoutInflater.from(parent.context).inflate(R.layout.project_grid_item_big, parent, false)

            NORMAL_ITEM -> v = LayoutInflater.from(parent.context).inflate(R.layout.project_grid_item_normal, parent, false)

            HEADER_ITEM -> v = LayoutInflater.from(parent.context).inflate(R.layout.projects_list_header, parent, false)
        }

        // Set the view to the ViewHolder
        val holder = ViewHolder(v!!, projectClickListener)
        return holder
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = getItemViewType(position)

        when (type) {
            NORMAL_ITEM, BIG_ITEM -> {
                val project = getItem(position)
                if (project != null) {
                    if (holder.projectId == project.id) {
                        // This view holder appears to already show correct project.
                        return
                    } else {
                        holder.projectId = project.id
                    }
                    ViewUtils.setTextForViewOrHideIt(holder.projectItemBigTitleTv, project.projectName)
                    holder.projectItemBigProgressSb!!.progress = project.percentProgress.toInt()

                    val picasso = Picasso.with(context)
                    val picassoBuilder: RequestCreator
                    if (type == BIG_ITEM) {
                        setProjectDetailsInfo(null, holder.projectItemBigDescTv!!, holder.projectItemBigGatheredMoneyTv!!,
                                holder.projectItemBigPledgedOfTv!!, holder.projectItemBigBackersTv!!,
                                holder.projectItemBigTimeLeft!!, holder.projectItemBigTimeLeftType!!, project)
                        picassoBuilder = picasso.load(project.bigPhotoUrl)
                                .resize(bigItemWidth, bigItemHeight)
                                .placeholder(R.drawable.blank_project_wide)
                    } else {
                        picassoBuilder = picasso.load(project.photoUrl)
                                .resize(smallItemWidth, smallItemHeight)
                                .placeholder(R.drawable.blank_project_small)
                    }
                    picassoBuilder.onlyScaleDown()
                            .transform(AplaTransformation())
                            .centerCrop()
                            .into(holder.projectItemBigPhotoIv)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewTypeProvider.getViewType(position)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        val size = dataset.size
        return size
    }

    override fun onClick(view: View) {
        //        ViewHolder holder = (ViewHolder) view.getTag();
        //        if (view.getId() == holder.mNameTextView.getId()) {
        //            Toast.makeText(context, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
        //        }
    }

    override fun onLongClick(view: View): Boolean {
        //        ViewHolder holder = (ViewHolder) view.getTag();
        //        if (view.getId() == holder.mNameTextView.getId()) {
        //            int pos = holder.getPosition();
        //            dataset.remove(pos);
        //
        //            // Call this method to refresh the list and display the "updated" list
        //            notifyItemRemoved(pos);
        //
        //            Toast.makeText(context, "Item " + holder.mNameTextView.getText() + " has been removed from list",
        //                    Toast.LENGTH_SHORT).show();
        //        }
        return false
    }

    fun setItems(items: MutableList<Project>) {
        synchronized(dataset) {
            if (showHeader) {
                //Fake project to simulate header
                items.add(0, Project())
            }
            val modified = dataset.setItems(items)
            if (modified) {
                notifyDataSetChanged()
            }
        }
    }

    fun addItems(items: Collection<Project>) {
        synchronized(dataset) {
            val modified = dataset.addAll(items)
            if (modified) {
                notifyDataSetChanged()
            }
        }
    }

    class ItemViewTypeProvider(private val showHeader: Boolean) {

        fun getViewType(position: Int): Int {
            if (showHeader) {
                if (position == 0) {
                    return HEADER_ITEM
                } else if (position % 5 == 3) {
                    return BIG_ITEM
                } else {
                    return NORMAL_ITEM
                }
            } else {
                if ((position + 2) % 5 == 4) {
                    return BIG_ITEM
                } else {
                    return NORMAL_ITEM
                }
            }
        }
    }

    // Create the ViewHolder class to keep references to your views
    class ViewHolder
    /**
     * Constructor

     * @param v The container view which holds the elements from the row item xml
     */
    (v: View, var mListener: ProjectClickListener?) : RecyclerView.ViewHolder(v), View.OnClickListener {

        internal var projectItemBigPhotoIv = v.findViewById<ImageView>(R.id.project_item_big_photo_iv)
        internal var projectItemBigTitleTv = v.findViewById<TextView>(R.id.project_item_big_title_tv)
        internal var projectItemBigDescTv = v.findViewById<TextView>(R.id.project_item_big_desc_tv)
        internal var projectItemBigProgressSb = v.findViewById<SeekBar>(R.id.project_item_big_progress_sb)
        internal var projectItemBigGatheredMoneyTv = v.findViewById<TextView>(R.id.project_item_big_gathered_money_tv)
        internal var projectItemBigBackersTv = v.findViewById<TextView>(R.id.project_item_big_backers_tv)
        internal var projectItemBigTimeLeft = v.findViewById<TextView>(R.id.project_item_big_days_left)
        internal var projectItemBigPledgedOfTv = v.findViewById<TextView>(R.id.project_item_big_pledged_of_tv)
        internal var projectItemBigTimeLeftType = v.findViewById<TextView>(R.id.project_item_time_left_type_tv)
        internal var projectItemBigBackersLabel = v.findViewById<View>(R.id.project_item_big_backers_label_tv)
        internal var projectId: Int = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (mListener != null && itemViewType != HEADER_ITEM) {
                val views = SharedViews(projectItemBigPhotoIv, projectItemBigTitleTv, projectItemBigTitleTv,
                        projectItemBigProgressSb,
                        projectItemBigGatheredMoneyTv, projectItemBigBackersTv, projectItemBigTimeLeft,
                        projectItemBigPledgedOfTv, projectItemBigBackersLabel, projectItemBigTimeLeftType)
                mListener!!.projectClicked(adapterPosition, views)
            }
        }
    }

    companion object {

        const val CURRENCY = "$"

        const val BIG_ITEM = 0
        const val NORMAL_ITEM = 1
        const val HEADER_ITEM = 2
        const val IMAGE_RATIO = (4 / 3).toDouble()

        fun setProjectDetailsInfo(gatheredMoneyTv: TextView, totalAmountTv: TextView, timeLeftValueTv: TextView, timeLeftTypeTv: TextView, project: Project) {
            gatheredMoneyTv.text = CURRENCY + project.getGatheredAmount()
            totalAmountTv.text = gatheredMoneyTv.context.getString(R.string.pledged_of, project.getTotalAmount())
            val timeLeft = project.getTimeLeft()
            timeLeftValueTv.text = timeLeft.value
            timeLeftTypeTv.text = timeLeft.description
        }

        fun setProjectDetailsInfo(title: TextView?, descTv: TextView, gatheredMoneyTv: TextView, totalAmountTv: TextView, backersTv: TextView, timeLeftValueTv: TextView, timeLeftTypeTv: TextView, project: Project) {
            ViewUtils.setText(title, project.projectName)
            ViewUtils.setTextForViewOrHideIt(descTv, project.desc)
            backersTv.text = Integer.toString(project.backers)
            setProjectDetailsInfo(gatheredMoneyTv, totalAmountTv, timeLeftValueTv, timeLeftTypeTv, project)
        }
    }
}

