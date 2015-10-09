package com.byoutline.kickmaterial.model;

import com.google.auto.value.AutoValue;

import java.util.Map;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@AutoValue
public abstract class ProjectIdAndSignature {
    public static ProjectIdAndSignature create(int id, Map<String, String> queryParams) {
        return new AutoValue_ProjectIdAndSignature(id, queryParams);
    }

    public abstract int id();

    public abstract Map<String, String> queryParams();
}
