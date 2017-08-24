package com.byoutline.kickmaterial.dagger

import android.content.SharedPreferences
import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.ibuscachedfield.util.RetrofitHelper.apiValueProv
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.adapters.SearchViewModel
import com.byoutline.kickmaterial.api.KickMaterialRequestInterceptor
import com.byoutline.kickmaterial.api.KickMaterialService
import com.byoutline.kickmaterial.events.*
import com.byoutline.kickmaterial.fragments.ProjectsListFragment
import com.byoutline.kickmaterial.managers.AccessTokenProvider
import com.byoutline.kickmaterial.managers.LoginManager
import com.byoutline.kickmaterial.managers.ProjectListViewModel
import com.byoutline.kickmaterial.model.*
import com.byoutline.kickmaterial.utils.LruCacheWithPlaceholders
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.ottocachedfield.CachedFieldBuilder
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.squareup.otto.Bus
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
class GlobalModule(private val app: KickMaterialApp, private val bus: Bus, private val accessTokenProvider: AccessTokenProvider) {
    private val picassoCache: LruCacheWithPlaceholders = LruCacheWithPlaceholders(app)

    init {
        try {
            Picasso.setSingletonInstance(Picasso.Builder(app).memoryCache(picassoCache).build())
        } catch (ex: IllegalStateException) {
            // singleton was already set
        }

    }

    @GlobalScope
    @Provides
    internal fun providesOttoBus(): Bus = bus

    @Provides
    internal fun providesApp(): KickMaterialApp = app

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

    @Provides
    @GlobalScope
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

    @Provides
    @GlobalScope
    fun provideATP(): AccessTokenProvider = AccessTokenProvider()

    @Provides
    @GlobalScope
    fun providesLoginManager(bus: Bus, service: KickMaterialService, accessTokenProvider: AccessTokenProvider): LoginManager {
        val instance = LoginManager(service, accessTokenProvider)
        bus.register(instance)
        return instance
    }

    @Provides
    @GlobalScope
    fun providesSharedPreferences(): SharedPreferences = app.sharedPrefs

    @Provides
    @Reusable
    fun provideProjectListViewModel(sharedPrefs: SharedPreferences): ProjectListViewModel {
        // show header on first launch
        val showHeader = sharedPrefs.getBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, true)
        sharedPrefs.edit().putBoolean(ProjectsListFragment.PREFS_SHOW_HEADER, false).apply()
        return ProjectListViewModel(showHeader)
    }

    @Provides
    @Reusable
    fun provideSearchViewModel() = SearchViewModel()

    @Provides
    @GlobalScope
    fun provideCategories(service: KickMaterialService): CachedField<List<Category>>
            = CachedFieldBuilder()
            .withValueProvider(apiValueProv<List<Category>>{ service.categories })
            .withSuccessEvent(CategoriesFetchedEvent())
            .build()

    @Provides
    @GlobalScope
    fun provideDiscover(service: KickMaterialService): CachedFieldWithArg<DiscoverResponse, DiscoverQuery>
            = CachedFieldBuilder()
            .withValueProviderWithArg(apiValueProv<DiscoverResponse, DiscoverQuery> { query -> service.getDiscover(query.queryMap) })
            .withSuccessEvent(DiscoverProjectsFetchedEvent())
            .withErrorEvent(DiscoverProjectsFetchedErrorEvent())
            .build()

    @Provides
    @GlobalScope
    fun provideProjectDetails(service: KickMaterialService): ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>
            = CachedFieldBuilder()
            .withValueProviderWithArg(apiValueProv<ProjectDetails, ProjectIdAndSignature> { (id, queryParams) -> service.getProjectDetails(id, queryParams) })
            .asObservable()
            .withSuccessEvent(ProjectDetailsFetchedEvent())
            .withErrorEvent(ProjectDetailsFetchingFailedEvent())
            .build()
}