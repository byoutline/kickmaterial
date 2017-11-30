package com.byoutline.kickmaterial.dagger

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.api.KickMaterialRequestInterceptor
import com.byoutline.kickmaterial.api.KickMaterialService
import com.byoutline.kickmaterial.features.projectdetails.LruCacheWithPlaceholders
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.model.ProjectIdAndSignature
import com.byoutline.observablecachedfield.ObservableCachedFieldBuilder
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.observablecachedfield.util.AndroidExecutor
import com.byoutline.observablecachedfield.util.RetrofitHelper.apiValueProv
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
open class AppModule(private val app: KickMaterialApp) {
    private val picassoCache: LruCacheWithPlaceholders = LruCacheWithPlaceholders(app)

    init {
        try {
            Picasso.setSingletonInstance(Picasso.Builder(app).memoryCache(picassoCache).build())
        } catch (ex: IllegalStateException) {
            // singleton was already set
        }
    }

    @Provides
    internal fun providesApp(): KickMaterialApp = app

    @Provides @AnimationDurationMultiplier
    open fun provideAnimationDurationMultiplier(): Int = 1

    @Provides
    internal fun providesPicassoCache(): LruCacheWithPlaceholders = picassoCache

    @Provides
    internal fun providesGson(): Gson {
        val builder = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        val deserializer = JsonDeserializer { json, _, _ ->
            DateTime(json.asJsonPrimitive.asLong * 1000)
        }
        builder.registerTypeAdapter(DateTime::class.java, deserializer)
        return builder.create()
    }

    @Provides @Singleton
    fun providesKickMaterialService(requestInterceptor: KickMaterialRequestInterceptor, gson: Gson): KickMaterialService
            = createService("http://localhost:8099", KickMaterialService::class.java, requestInterceptor, gson)

    private fun <T> createService(endpoint: String, serviceClass: Class<T>,
                                  requestInterceptor: KickMaterialRequestInterceptor?, gson: Gson): T {
        val clientBuilder = OkHttpClient.Builder()
        if (requestInterceptor != null) {
            clientBuilder.addInterceptor(requestInterceptor)
        }


        val builder = Retrofit.Builder()

        builder.baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())


        return builder.build().create(serviceClass)
    }

    @Provides @Reusable
    open fun provideSharedPrefs(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    @Provides @Singleton
    fun provideDiscover(service: KickMaterialService): ObservableCachedFieldWithArg<DiscoverResponse, DiscoverQuery> {
        return ObservableCachedFieldBuilder()
                .withValueProviderWithArg(apiValueProv<DiscoverResponse, DiscoverQuery> { query -> service.getDiscover(query.queryMap) })
                .withCustomStateListenerExecutor(AndroidExecutor.MAIN_THREAD_EXECUTOR)
                .build()
    }

    @Provides @Singleton
    fun provideProjectDetails(service: KickMaterialService): ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>
            = ObservableCachedFieldBuilder()
            .withValueProviderWithArg(apiValueProv<ProjectDetails, ProjectIdAndSignature> { (id, queryParams) -> service.getProjectDetails(id, queryParams) })
            .build()
}