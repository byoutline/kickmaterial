package com.byoutline.kickmaterial.model;

import org.parceler.Parcel;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@Parcel(Parcel.Serialization.FIELD)
public class ProjectVideo {
    public int id;
    public String status;
    public String high;
    public String base;
    public String webm;
    public int width;
    public int height;
    public String frame;
}
