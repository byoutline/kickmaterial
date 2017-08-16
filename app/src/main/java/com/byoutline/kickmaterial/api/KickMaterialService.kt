package com.byoutline.kickmaterial.api

import com.byoutline.kickmaterial.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
interface KickMaterialService {

    @get:GET("v1/categories")
    val categories: Call<List<Category>>

    @GET("v1/discover")
    fun getDiscover(@QueryMap parameters: Map<String, String>): Call<DiscoverResponse>

    @GET("v1/projects/{project_id}")
    fun getProjectDetails(@Path("project_id") projectId: Int, @QueryMap parameters: Map<String, String>): Call<ProjectDetails>

    @POST("xauth/access_token")
    fun postGetAccessToken(@Body emailAndPass: EmailAndPass): Call<AccessToken>
}
