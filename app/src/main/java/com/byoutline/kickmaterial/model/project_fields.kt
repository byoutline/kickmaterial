package com.byoutline.kickmaterial.model

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
class ProjectCreator(val id: Int,
                     val name: String?,
                     val avatar: ProjectCreatorAvatar?,
                     val urls: CreatorUrls?) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectCreator.CREATOR
    }
}

@PaperParcel
class ProjectPhoto(
        val full: String?,
        val ed: String?,
        val med: String?,
        val little: String?,
        val small: String?,
        val thumb: String?,
        @SerializedName("1024x768") val size1024x768: String?,
        @SerializedName("1536x1152") val size1536x1152: String?
) : PaperParcelable {


    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectPhoto.CREATOR
    }
}

@PaperParcel
class ProjectUrls(val api: ProjectUrlsApi?,
                  val web: WebUrlsApi?) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectUrls.CREATOR
    }
}

@PaperParcel
class ProjectVideo(
        val id: Int,
        val status: String?,
        val high: String?,
        val base: String?,
        val webm: String?,
        val width: Int,
        val height: Int,
        val frame: String?
) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectVideo.CREATOR
    }
}




@PaperParcel
class ProjectUrlsApi(val project: String?) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectUrlsApi.CREATOR
    }
}

@PaperParcel
class WebUrlsApi(val project: String?,
                 @SerializedName("project_short") val projectShort: String?) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelWebUrlsApi.CREATOR
    }
}

@PaperParcel
class ProjectCreatorAvatar(val thumb: String?,
                           val small: String?,
                           val medium: String?) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectCreatorAvatar.CREATOR
    }
}

class ProjectTime(val value: String, val description: String)

@PaperParcel
class CreatorUrls(val web: CreatorUrlsWeb?) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCreatorUrls.CREATOR
    }
}

@PaperParcel
class CreatorUrlsWeb(val user: String?) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCreatorUrlsWeb.CREATOR
    }
}