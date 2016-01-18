package com.byoutline.kickmaterial.api;

import com.byoutline.kickmaterial.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Sebastian Kacprzak on 24.03.15.
 */
public interface KickMaterialService {

    @GET("v1/categories")
    Call<List<Category>> getCategories();

    @GET("v1/discover")
    Call<DiscoverResponse> getDiscover(@QueryMap Map<String, String> parameters);

    @GET("v1/projects/{project_id}")
    Call<ProjectDetails> getProjectDetails(@Path("project_id") int projectId, @QueryMap Map<String, String> parameters);

    @POST("xauth/access_token")
    Call<AccessToken> postGetAccessToken(@Body EmailAndPass emailAndPass);
}
