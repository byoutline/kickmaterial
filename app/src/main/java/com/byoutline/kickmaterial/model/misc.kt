package com.byoutline.kickmaterial.model

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 24.03.15.
 */
class AccessToken(val accessToken: String? = null)

class EmailAndPass(val email: String, val password: String)

data class ProjectIdAndSignature(val id: Int, val queryParams: Map<String, String>)
