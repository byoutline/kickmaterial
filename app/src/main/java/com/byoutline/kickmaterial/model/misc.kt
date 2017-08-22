package com.byoutline.kickmaterial.model

import com.byoutline.kickmaterial.utils.QueryParamsExtractor
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 24.03.15.
 */
class AccessToken(var accessToken: String? = null)

@PaperParcel
class CreatorUrls(var web: CreatorUrlsWeb?) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCreatorUrls.CREATOR
    }
}

@PaperParcel
class CreatorUrlsWeb(var user: String?) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCreatorUrlsWeb.CREATOR
    }
}

class DiscoverResponse(var projects: List<Project>?, var urls: DiscoverUrls?) {
    val moreProjectsUrl: Map<String, String>
        get() = QueryParamsExtractor.getQueryParams(urls!!.api.moreProjects!!)
}

enum class DiscoverType {
    DISCOVER, DISCOVER_MORE, DISCOVER_CATEGORY, SEARCH
}

class DiscoverUrls(var api: DiscoverUrlsApi)

class DiscoverUrlsApi(var moreProjects: String?)

class EmailAndPass(val email: String, val password: String)

@PaperParcel
class ProjectUrls : PaperParcelable {
    var api: ProjectUrlsApi? = null
    var web: WebUrlsApi? = null

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectUrls.CREATOR
    }
}

@PaperParcel
class ProjectUrlsApi : PaperParcelable {
    var project: String? = null

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectUrlsApi.CREATOR
    }
}

@PaperParcel
class WebUrlsApi : PaperParcelable {

    var project: String? = null
    @SerializedName("project_short")
    var projectShort: String? = null

    companion object {
        @JvmField
        val CREATOR = PaperParcelWebUrlsApi.CREATOR
    }
}

data class ProjectIdAndSignature(val id: Int, val queryParams: Map<String, String>)
