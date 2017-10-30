package com.byoutline.kickmaterial

import android.app.Application
import android.support.annotation.VisibleForTesting
import com.byoutline.androidstubserver.AndroidStubServer
import com.byoutline.cachedfield.utils.SameSessionIdProvider
import com.byoutline.ibuscachedfield.util.RetrofitHelper
import com.byoutline.kickmaterial.dagger.DaggerGlobalComponent
import com.byoutline.kickmaterial.dagger.GlobalComponent
import com.byoutline.kickmaterial.dagger.GlobalModule
import com.byoutline.mockserver.NetworkType
import com.byoutline.ottocachedfield.OttoCachedField
import com.byoutline.secretsauce.Settings
import com.byoutline.secretsauce.utils.showToast
import timber.log.Timber

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class KickMaterialApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidStubServer.start(this, NetworkType.UMTS)
        RetrofitHelper.MSG_DISPLAYER = RetrofitHelper.MsgDisplayer { msg -> this.showToast(msg, true) }

        resetComponents()
    }
    @VisibleForTesting
    @Synchronized fun setComponents(mainComponent: GlobalComponent) {
        component = mainComponent
        Settings.set(debug = BuildConfig.DEBUG, containerViewId =  R.id.container)
        OttoCachedField.init(SameSessionIdProvider(), mainComponent.bus)
    }

    private fun resetComponents() {
        val mainComponent = createGlobalComponent()
        setComponents(mainComponent)
    }

    private fun createGlobalComponent(): GlobalComponent {
        return DaggerGlobalComponent.builder()
                .globalModule(GlobalModule(this))
                .build()
    }

    companion object {
        lateinit var component: GlobalComponent
    }
}
