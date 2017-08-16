package com.byoutline.kickmaterial.model

import android.databinding.BaseObservable
import java.util.*

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class DiscoverQuery(val queryMap: Map<String, String>, val discoverType: DiscoverType) : BaseObservable() {

    val pageFromQuery: Int?
        get() {
            if (queryMap.containsKey("page")) {
                return Integer.valueOf(queryMap["page"])
            }
            return null
        }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as DiscoverQuery?

        if (queryMap != that!!.queryMap ) return false
        return discoverType == that.discoverType

    }

    override fun hashCode(): Int {
        var result = queryMap.hashCode()
        result = 31 * result + discoverType.hashCode()
        return result
    }

    companion object {
        private const val PER_PAGE = 12

        fun getDiscover(page: Int?): DiscoverQuery {
            val firstPage = page == null || page === 1
            val params = if (firstPage) emptyMap<String, String>() else getDiscoverCategoryMap(null, page, PER_PAGE, null)
            return DiscoverQuery(params, DiscoverType.DISCOVER)
        }

        fun getDiscoverMore(params: Map<String, String>): DiscoverQuery {
            return DiscoverQuery(params, DiscoverType.DISCOVER_MORE)
        }

        fun getDiscoverCategory(categoryId: Int, page: Int, sort: SortTypes): DiscoverQuery {
            val params = getDiscoverCategoryMap(categoryId, page, PER_PAGE, sort)
            return DiscoverQuery(params, DiscoverType.DISCOVER_CATEGORY)
        }

        fun getDiscoverSearch(searchTerm: String, categoryId: Int?, page: Int?, sort: SortTypes): DiscoverQuery {
            val params = getDiscoverCategoryMap(categoryId, page, PER_PAGE, sort)
            params.put("term", searchTerm)
            return DiscoverQuery(params, DiscoverType.SEARCH)
        }

        private fun getDiscoverCategoryMap(categoryId: Int?, page: Int?, perPage: Int?, sort: SortTypes?): MutableMap<String, String> {
            val params = HashMap<String, String>()
            if (categoryId != null) {
                params.put("category_id", Integer.toString(categoryId))
            }
            if (page != null) {
                params.put("page", Integer.toString(page))
            }
            if (perPage != null) {
                params.put("per_page", Integer.toString(perPage))
            }
            if (sort != null) {
                params.put("sort", sort.apiName)
            }
            return params
        }

        fun getDiscoverQuery(category: Category?, page: Int): DiscoverQuery {
            if (category == null || category.categoryId == Category.ALL_CATEGORIES_ID) {
                return getDiscover(page)
            } else {
                return getDiscoverCategory(category.categoryId, page, SortTypes.MAGIC)
            }
        }
    }
}
