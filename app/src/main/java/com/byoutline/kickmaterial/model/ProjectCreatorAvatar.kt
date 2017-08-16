package com.byoutline.kickmaterial.model

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
@PaperParcel
class ProjectCreatorAvatar: PaperParcelable {

    var thumb: String? = null
    var small: String? = null
    var medium: String? = null

    companion object {
        @JvmField val CREATOR = PaperParcelProjectCreatorAvatar.CREATOR
    }
}
