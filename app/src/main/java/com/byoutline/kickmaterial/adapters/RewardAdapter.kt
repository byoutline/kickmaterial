package com.byoutline.kickmaterial.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.model.Reward
import com.byoutline.kickmaterial.model.RewardItem
import com.byoutline.secretsauce.utils.ViewUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class RewardAdapter// Adapter's Constructor
(private val context: Context, private val rewardClickListener: RewardClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val dataset = ArrayList<RewardItem>()
    private val semiGreenColor: Int = ContextCompat.getColor(context, R.color.green_light)


    fun getItem(position: Int) = dataset[position]

    // Create new views. This is invoked by the layout manager.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        // Create a new view by inflating the row item xml.
        var holder: RecyclerView.ViewHolder? = null
        when (viewType) {
            RewardItem.ITEM -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.reward_list_item, parent, false)
                holder = RewardViewHolder(v, rewardClickListener)
            }

            RewardItem.HEADER -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.reward_header_item, parent, false)
                holder = RewardHeaderViewHolder(v)
            }
        }

        // Set the view to the ViewHolder

        return holder
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)

        when (type) {
            RewardItem.ITEM -> {
                val rewardHolder = holder as RewardViewHolder
                val reward = getItem(position) as Reward?

                if (position == 0) {
                    rewardHolder.rewardItemContainerCv!!.setCardBackgroundColor(semiGreenColor)
                } else {
                    rewardHolder.rewardItemContainerCv!!.setCardBackgroundColor(Color.WHITE)
                }

                if (reward != null) {

                    rewardHolder.rewardItemAmountTv!!.text = context.getString(R.string.reward_value, reward.minimum)
                    //                    ViewUtils.setTextOrClear(rewardHolder.rewardItemAmountTv, Double.toString(reward.minimum));
                    ViewUtils.setTextOrClear(rewardHolder.rewardItemDescTv, reward.description)
                    ViewUtils.setTextOrClear(rewardHolder.rewardItemTitleTv, reward.reward)
                }
            }

            RewardItem.HEADER -> {
                val headerHolder = holder as RewardHeaderViewHolder
                val header = getItem(position) as RewardHeader?

                headerHolder.rewardHeaderTv!!.text = context.getString(R.string.reward_more_than, header!!.minimum)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataset.size
    }

    fun setItems(items: List<Reward>) {
        synchronized(dataset) {
            var currentTreshold = 100.0
            val step = 10.0
            dataset.clear()

            val rewardItems = ArrayList<RewardItem>()

            for (reward in items) {

                if (reward.minimum >= currentTreshold) {
                    rewardItems.add(RewardHeader(currentTreshold.toInt()))
                    currentTreshold *= step
                }
                rewardItems.add(reward)
            }

            dataset.addAll(rewardItems)
            notifyDataSetChanged()
            rewardItems.clear()
        }
    }


    class RewardHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField @BindView(R.id.reward_header_tv)
        var rewardHeaderTv: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}


class RewardViewHolder(v: View, var listener: RewardClickListener?) : RecyclerView.ViewHolder(v), View.OnClickListener {
    @JvmField @BindView(R.id.reward_item_amount_tv)
    var rewardItemAmountTv: TextView? = null
    @JvmField @BindView(R.id.reward_item_currency_tv)
    var rewardItemCurrencyTv: TextView? = null
    @JvmField @BindView(R.id.reward_item_type_tv)
    var rewardItemTitleTv: TextView? = null
    @JvmField @BindView(R.id.reward_item_desc_tv)
    var rewardItemDescTv: TextView? = null

    @JvmField @BindView(R.id.reward_container_cv)
    var rewardItemContainerCv: CardView? = null


    init {
        ButterKnife.bind(this, v)
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (listener != null) {
            listener!!.rewardClicked(position)
        }
    }
}


@PaperParcel
internal class RewardHeader(var minimum: Int) : RewardItem, PaperParcelable {

    override val itemType: Int
        get() = RewardItem.HEADER

    companion object {
        @JvmField val CREATOR = PaperParcelRewardHeader.CREATOR
    }
}