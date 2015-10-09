package com.byoutline.kickmaterial.model;

import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-03-30
 */
@Parcel(Parcel.Serialization.FIELD)
public class WebUrlsApi {

    public String project;
    @SerializedName("project_short")
    public String projectShort;
}
