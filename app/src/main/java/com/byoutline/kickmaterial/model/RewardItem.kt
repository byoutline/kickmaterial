package com.byoutline.kickmaterial.model

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
interface RewardItem {

    val itemType: Int

    companion object {
        const val ITEM = 0
        const val HEADER = 1
    }
}
