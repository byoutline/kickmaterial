package com.byoutline.kickmaterial.api

import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.kickmaterial.model.ProjectDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
interface KickMaterialService {

    @GET("v1/discover")
    fun getDiscover(@QueryMap parameters: Map<String, String>): Call<DiscoverResponse>

    @GET("v1/projects/{project_id}")
    fun getProjectDetails(@Path("project_id") projectId: Int, @QueryMap parameters: Map<String, String>): Call<ProjectDetails>
}
