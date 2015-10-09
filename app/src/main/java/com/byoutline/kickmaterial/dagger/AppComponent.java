package com.byoutline.kickmaterial.dagger;


import com.byoutline.secretsauce.di.AppComponentInterface;
import dagger.Component;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.03.15.
 */
@AppScope
@Component(dependencies = GlobalComponent.class, modules = AppModule.class)
public interface AppComponent extends AppComponentInterface {
}
