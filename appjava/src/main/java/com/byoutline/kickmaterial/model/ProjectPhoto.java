package com.byoutline.kickmaterial.model;

import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

/**
 * Created by Sebastian Kacprzak on 25.03.15.
 */
@Parcel(Parcel.Serialization.FIELD)
public class ProjectPhoto {
    public String full;
    public String ed;
    public String med;
    public String little;
    public String small;
    public String thumb;
    @SerializedName("1024x768")
    public String size1024x768;
    @SerializedName("1536x1152")
    public String size1536x1152;
}
