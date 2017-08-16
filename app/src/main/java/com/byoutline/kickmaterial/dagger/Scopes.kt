package com.byoutline.kickmaterial.dagger

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GlobalScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope