package com.byoutline.kickmaterial.events;

import com.byoutline.kickmaterial.model.DiscoverQuery;
import com.byoutline.kickmaterial.model.DiscoverResponse;
import com.byoutline.ottocachedfield.events.ResponseEventWithArgImpl;

/**
 * Created by Sebastian Kacprzak on 25.03.15.
 */
public class DiscoverProjectsFetchedEvent extends ResponseEventWithArgImpl<DiscoverResponse, DiscoverQuery> {
}
