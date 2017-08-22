package com.byoutline.kickmaterial.model

import org.joda.time.DateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
class Reward : RewardItem, PaperParcelable {

    var minimum: Double = 0.toDouble()
    var reward: String? = null
    // optional fields
    var description: String? = null
    var shippingEnabled: Boolean = false
    var estimatedDeliveryOn: DateTime? = null
    var updatedAt: DateTime? = null
    var backersCount: Int = 0

    override val itemType: Int
        get() = RewardItem.ITEM

    override fun toString(): String {
        return "Reward(minimum=$minimum, reward=$reward, description=$description)"
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelReward.CREATOR
    }
}