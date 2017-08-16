package com.byoutline.kickmaterial.model

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Sebastian Kacprzak on 25.03.15.
 */
@PaperParcel
class ProjectPhoto: PaperParcelable  {
    var full: String? = null
    var ed: String? = null
    var med: String? = null
    var little: String? = null
    var small: String? = null
    var thumb: String? = null
    @SerializedName("1024x768")
    var size1024x768: String? = null
    @SerializedName("1536x1152")
    var size1536x1152: String? = null

    companion object {
        @JvmField val CREATOR = PaperParcelProjectPhoto.CREATOR
    }
}
