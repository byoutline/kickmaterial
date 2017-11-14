package com.byoutline.kickmaterial.model

import org.joda.time.DateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
@PaperParcel
class Reward(
        val minimum: Double,
        val reward: String?,
        val description: String? = null,
        val shippingEnabled: Boolean = false,
        val estimatedDeliveryOn: DateTime? = null,
        val updatedAt: DateTime? = null,
        val backersCount: Int = 0
) : RewardItem, PaperParcelable {


    override val itemType: Int
        get() = RewardItem.ITEM

    override fun toString() =
            "Reward(minimum=$minimum, reward=$reward, description=$description)"

    companion object {
        @JvmField
        val CREATOR = PaperParcelReward.CREATOR
    }
}

interface RewardItem {

    val itemType: Int

    companion object {
        const val ITEM = 0
        const val HEADER = 1
    }
}
