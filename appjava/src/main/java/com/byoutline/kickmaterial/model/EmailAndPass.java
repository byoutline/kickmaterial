package com.byoutline.kickmaterial.model;

import com.google.auto.value.AutoValue;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@AutoValue
public abstract class EmailAndPass {
    public static EmailAndPass create(String email, String password) {
        return new AutoValue_EmailAndPass(email, password);
    }

    public abstract String email();

    public abstract String password();
}
