package com.byoutline.kickmaterial

import android.support.annotation.VisibleForTesting
import com.byoutline.androidstubserver.AndroidStubServer
import com.byoutline.ibuscachedfield.util.RetrofitHelper
import com.byoutline.kickmaterial.dagger.*
import com.byoutline.kickmaterial.login.AccessTokenProvider
import com.byoutline.mockserver.NetworkType
import com.byoutline.secretsauce.BaseApp
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.otto.Bus
import timber.log.Timber

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class KickMaterialApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidStubServer.start(this, NetworkType.UMTS)
        RetrofitHelper.MSG_DISPLAYER = RetrofitHelper.MsgDisplayer { msg -> ViewUtils.showToast(msg, true) }

        resetComponents()
    }


    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    @VisibleForTesting
    @Synchronized fun setComponents(mainComponent: GlobalComponent, appComponent: AppComponent) {
        component = mainComponent
        init(appComponent)
        //        component.inject(this);
    }

    fun resetComponents() {
        val appComponent = createAppComponent()
        val mainComponent = createGlobalComponent(appComponent.bus, appComponent.accessTokenProvider)
        setComponents(mainComponent, appComponent)
    }

    private fun createGlobalComponent(bus: Bus, accessTokenProvider: AccessTokenProvider): GlobalComponent {
        return DaggerGlobalComponent.builder()
                .globalModule(GlobalModule(this, bus, accessTokenProvider))
                .build()
    }

    private fun createAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {
        lateinit var component: GlobalComponent
    }
}
