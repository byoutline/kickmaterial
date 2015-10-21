package com.byoutline.kickmaterial.dagger;

import android.content.SharedPreferences;
import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.eventcallback.IBus;
import com.byoutline.kickmaterial.BuildConfig;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.api.ApiErrorHandler;
import com.byoutline.kickmaterial.api.KickMaterialRequestInterceptor;
import com.byoutline.kickmaterial.api.KickMaterialService;
import com.byoutline.kickmaterial.events.*;
import com.byoutline.kickmaterial.managers.AccessTokenProvider;
import com.byoutline.kickmaterial.managers.LoginManager;
import com.byoutline.kickmaterial.model.DiscoverQuery;
import com.byoutline.kickmaterial.utils.LruCacheWithPlaceholders;
import com.byoutline.ottocachedfield.ObservableCachedFieldWithArg;
import com.byoutline.ottocachedfield.OttoCachedFieldBuilder;
import com.byoutline.ottocachedfield.OttoCachedFieldWithArgBuilder;
import com.byoutline.ottoeventcallback.PostFromAnyThreadBus;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import org.joda.time.DateTime;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import javax.annotation.Nullable;
import java.util.List;


@Module
public class GlobalModule {

    private final KickMaterialApp app;
    private LruCacheWithPlaceholders picassoCache;

    public GlobalModule(KickMaterialApp app) {
        this.app = app;
        picassoCache = new LruCacheWithPlaceholders(app);
        Picasso.setSingletonInstance(new Picasso.Builder(app).memoryCache(picassoCache).build());
    }

    @GlobalScope
    @Provides
    Bus providesOttoBus(IBus bus) {
        return (Bus) bus;
    }

    @GlobalScope
    @Provides
    IBus provideBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    KickMaterialApp providesApp() {
        return app;
    }

    @Provides
    LruCacheWithPlaceholders providesPicassoCache() {
        return picassoCache;
    }


    @Provides
    Gson providesGson() {
        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        JsonDeserializer<DateTime> deserializer = (json, typeOfT, context) -> new DateTime(json.getAsJsonPrimitive().getAsLong() * 1000);
        builder.registerTypeAdapter(DateTime.class, deserializer);
        return builder.create();
    }

    @Provides
    @GlobalScope
    public KickMaterialService providesKickMaterialService(KickMaterialRequestInterceptor requestInterceptor, Gson gson, ApiErrorHandler errorHandler) {
        return createService("http://localhost:8099", KickMaterialService.class, requestInterceptor, gson, errorHandler);
    }

    private <T> T createService(String endpoint, Class<T> serviceClass, @Nullable KickMaterialRequestInterceptor requestInterceptor, Gson gson, ApiErrorHandler errorHandler) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(endpoint)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setErrorHandler(errorHandler)
                .setConverter(new GsonConverter(gson));
        if (requestInterceptor != null) {
            builder.setRequestInterceptor(requestInterceptor);
        }

        return builder.build().create(serviceClass);
    }

    @Provides
    @GlobalScope
    public LoginManager providesLoginManager(Bus bus, KickMaterialService service, AccessTokenProvider accessTokenProvider) {
        LoginManager instance = new LoginManager(service, accessTokenProvider);
        bus.register(instance);
        return instance;
    }

    @Provides
    @GlobalScope
    public SharedPreferences providesSharedPreferences() {
        return app.getSharedPrefs();
    }

    @Provides
    @GlobalScope
    public CachedField<List<Category>> provideCategories(KickMaterialService service) {
        return new OttoCachedFieldBuilder<List<Category>>()
                .withValueProvider(service::getCategories)
                .withSuccessEvent(new CategoriesFetchedEvent())
                .build();
    }

    @Provides
    @GlobalScope
    public CachedFieldWithArg<DiscoverResponse, DiscoverQuery> provideDiscover(KickMaterialService service) {
        return new OttoCachedFieldWithArgBuilder<DiscoverResponse, DiscoverQuery>()
                .withValueProvider(query -> service.getDiscover(query.queryMap))
                .withSuccessEvent(new DiscoverProjectsFetchedEvent())
                .withResponseErrorEvent(new DiscoverProjectsFetchedErrorEvent())
                .build();
    }

    @Provides
    @GlobalScope
    public ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>
    provideProjectDetails(KickMaterialService service) {
        return ObservableCachedFieldWithArg.<ProjectDetails, ProjectIdAndSignature>builder()
                .withValueProvider(input -> service.getProjectDetails(input.id(), input.queryParams()))
                .withSuccessEvent(new ProjectDetailsFetchedEvent())
                .withResponseErrorEvent(new ProjectDetailsFetchingFailedEvent())
                .build();
    }
}
