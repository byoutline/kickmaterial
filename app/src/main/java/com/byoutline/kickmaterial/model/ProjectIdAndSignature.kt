package com.byoutline.kickmaterial.model

import com.google.auto.value.AutoValue

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@AutoValue
abstract class ProjectIdAndSignature {

    abstract fun id(): Int

    abstract fun queryParams(): Map<String, String>

    companion object {
        fun create(id: Int, queryParams: Map<String, String>): ProjectIdAndSignature {
            return AutoValue_ProjectIdAndSignature(id, queryParams)
        }
    }
}
