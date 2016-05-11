package com.byoutline.kickmaterial.model;

import android.databinding.BaseObservable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DiscoverQuery extends BaseObservable {
    @Nonnull
    public final Map<String, String> queryMap;
    @Nonnull
    public final DiscoverType discoverType;
    private static final int PER_PAGE = 12;

    public DiscoverQuery(@Nonnull Map<String, String> queryMap, @Nonnull DiscoverType discoverType) {
        this.queryMap = queryMap;
        this.discoverType = discoverType;
    }

    public Integer getPageFromQuery() {
        if(queryMap != null && queryMap.containsKey("page")) {
            return Integer.valueOf(queryMap.get("page"));
        }
        return null;
    }

    public static DiscoverQuery getDiscover(@Nullable Integer page) {
        boolean firstPage = page == null || page == 1;
        Map<String, String> params = firstPage ? Collections.emptyMap() : getDiscoverCategoryMap(null, page, PER_PAGE, null);
        return new DiscoverQuery(params, DiscoverType.DISCOVER);
    }

    public static DiscoverQuery getDiscoverMore(Map<String, String> params) {
        return new DiscoverQuery(params, DiscoverType.DISCOVER_MORE);
    }

    public static DiscoverQuery getDiscoverCategory(int categoryId, int page, @Nonnull SortTypes sort) {
        Map<String, String> params = getDiscoverCategoryMap(categoryId, page, PER_PAGE, sort);
        return new DiscoverQuery(params, DiscoverType.DISCOVER_CATEGORY);
    }

    public static DiscoverQuery getDiscoverSearch(@Nonnull String searchTerm, @Nullable Integer categoryId, @Nullable Integer page, @Nonnull SortTypes sort) {
        Map<String, String> params = getDiscoverCategoryMap(categoryId, page, PER_PAGE, sort);
        params.put("term", searchTerm);
        return new DiscoverQuery(params, DiscoverType.SEARCH);
    }

    private static Map<String, String> getDiscoverCategoryMap(@Nullable Integer categoryId, @Nullable Integer page, @Nullable Integer perPage, @Nullable SortTypes sort) {
        Map<String, String> params = new HashMap<>();
        if (categoryId != null) {
            params.put("category_id", Integer.toString(categoryId));
        }
        if (page != null) {
            params.put("page", Integer.toString(page));
        }
        if (perPage != null) {
            params.put("per_page", Integer.toString(perPage));
        }
        if (sort != null) {
            params.put("sort", sort.getApiName());
        }
        return params;
    }

    public static DiscoverQuery getDiscoverQuery(Category category, int page) {
        if (category == null || category.categoryId == Category.ALL_CATEGORIES_ID) {
            return getDiscover(page);
        } else {
            return getDiscoverCategory(category.categoryId, page, SortTypes.MAGIC);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscoverQuery that = (DiscoverQuery) o;

        if (queryMap != null ? !queryMap.equals(that.queryMap) : that.queryMap != null) return false;
        return discoverType == that.discoverType;

    }

    @Override
    public int hashCode() {
        int result = queryMap != null ? queryMap.hashCode() : 0;
        result = 31 * result + discoverType.hashCode();
        return result;
    }
}
