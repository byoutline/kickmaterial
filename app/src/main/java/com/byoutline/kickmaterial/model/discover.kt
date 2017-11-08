package com.byoutline.kickmaterial.model

import com.byoutline.kickmaterial.model.utils.QueryParamsExtractor


class DiscoverResponse(val projects: List<Project>?, val urls: DiscoverUrls?) {
    val moreProjectsUrl: Map<String, String>
        get() = QueryParamsExtractor.getQueryParams(urls!!.api.moreProjects!!)
}

enum class DiscoverType {
    DISCOVER, DISCOVER_MORE, DISCOVER_CATEGORY, SEARCH
}

class DiscoverUrls(val api: DiscoverUrlsApi)

class DiscoverUrlsApi(val moreProjects: String?)