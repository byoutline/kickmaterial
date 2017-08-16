package com.byoutline.kickmaterial.events

import com.byoutline.eventcallback.ResponseEventImpl
import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl
import com.byoutline.kickmaterial.model.*

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
class AccessTokenFetchedEvent : ResponseEventWithArgImpl<AccessToken, EmailAndPass>()

class AccessTokenFetchingFailedEvent : ResponseEventWithArgImpl<Exception, EmailAndPass>()

class CategoriesFetchedEvent : ResponseEventImpl<List<Category>>()

class DiscoverProjectsFetchedErrorEvent : ResponseEventWithArgImpl<Exception, DiscoverQuery>()

class DiscoverProjectsFetchedEvent : ResponseEventWithArgImpl<DiscoverResponse, DiscoverQuery>()

class ProjectDetailsFetchedEvent : ResponseEventWithArgImpl<ProjectDetails, ProjectIdAndSignature>()

class ProjectDetailsFetchingFailedEvent : ResponseEventWithArgImpl<Exception, ProjectIdAndSignature>()
