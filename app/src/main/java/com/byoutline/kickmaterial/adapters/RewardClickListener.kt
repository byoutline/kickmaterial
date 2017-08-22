package com.byoutline.kickmaterial.adapters

import com.byoutline.kickmaterial.model.RewardItem

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-25
 */
interface RewardClickListener {

    fun rewardClicked(reward: RewardItem)
}
