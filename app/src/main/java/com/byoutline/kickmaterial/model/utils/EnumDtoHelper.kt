package com.byoutline.kickmaterial.model.utils

import com.byoutline.kickmaterial.KickMaterialApp

/**
 * @author Sebastian Kacprzak <nait at naitbit.com> on 11.03.2014
</nait> */
object EnumDtoHelper {

    fun getDisplayName(nameResId: Int, fallbackName: String): String {
        val instance = KickMaterialApp.component.app
        if (instance == null) {
            return fallbackName
        } else {
            return instance.applicationContext.getString(nameResId)
        }
    }
}
