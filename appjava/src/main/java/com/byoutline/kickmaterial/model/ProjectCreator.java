package com.byoutline.kickmaterial.model;

import org.parceler.Parcel;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
@Parcel(Parcel.Serialization.FIELD)
public class ProjectCreator {

    public int id;
    public String name;
    public ProjectCreatorAvatar avatar;
    public CreatorUrls urls;
}
