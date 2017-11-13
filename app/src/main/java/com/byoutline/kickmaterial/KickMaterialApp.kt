package com.byoutline.kickmaterial

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.byoutline.androidstubserver.AndroidStubServer
import com.byoutline.kickmaterial.dagger.AppComponent
import com.byoutline.kickmaterial.dagger.AppModule
import com.byoutline.kickmaterial.dagger.DaggerAppComponent
import com.byoutline.mockserver.NetworkType
import com.byoutline.observablecachedfield.util.RetrofitHelper
import com.byoutline.secretsauce.SecretSauceSettings
import com.byoutline.secretsauce.di.AppInjector
import com.byoutline.secretsauce.utils.showToast
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
open class KickMaterialApp : Application(), HasActivityInjector {

    @Inject
    open lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    open lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun activityInjector() = dispatchingActivityInjector

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidStubServer.start(this, NetworkType.UMTS)
        RetrofitHelper.setMsgDisplayer { msg -> this.showToast(msg, true) }

        initDagger()
    }

    @VisibleForTesting
    open fun initDagger() {
        setComponents(createGlobalComponent())
    }

    @VisibleForTesting
    open fun setComponents(appComponent: AppComponent) {
        component = appComponent
        component.inject(this)
        AppInjector.init(this)
        SecretSauceSettings.set(debug = BuildConfig.DEBUG,
                containerViewId = R.id.container,
                bindingViewModelId = BR.viewModel,
                viewModelFactoryProvider = ::getViewModelFactory)
    }

    private fun createGlobalComponent(): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {
        lateinit var component: AppComponent
            private set
    }
}

private fun getViewModelFactory(ctx: Context): ViewModelProvider.Factory
    = (ctx.applicationContext as KickMaterialApp).viewModelFactory