package com.byoutline.kickmaterial.events;

import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl;
import com.byoutline.kickmaterial.model.ProjectIdAndSignature;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 26.03.15.
 */
public class ProjectDetailsFetchingFailedEvent extends ResponseEventWithArgImpl<Exception, ProjectIdAndSignature> {
}