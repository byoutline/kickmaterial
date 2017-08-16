package com.byoutline.kickmaterial.model

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
@PaperParcel
class ProjectCreator : PaperParcelable {

    var id: Int = 0
    var name: String? = null
    var avatar: ProjectCreatorAvatar? = null
    var urls: CreatorUrls? = null

    companion object {
        @JvmField val CREATOR = PaperParcelProjectCreator.CREATOR
    }
}
