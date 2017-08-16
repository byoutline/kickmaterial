package com.byoutline.kickmaterial.model


import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@PaperParcel
class ProjectVideo : PaperParcelable {
    var id: Int = 0
    var status: String? = null
    var high: String? = null
    var base: String? = null
    var webm: String? = null
    var width: Int = 0
    var height: Int = 0
    var frame: String? = null

    companion object {
        @JvmField val CREATOR = PaperParcelProjectVideo.CREATOR
    }
}
