package com.byoutline.kickmaterial.model;

import org.parceler.Parcel;

import java.util.List;

@Parcel(Parcel.Serialization.FIELD)
public class ProjectDetails extends Project {

    public List<Reward> rewards;
    public int commentsCount;
    public int updatesCount;
    public ProjectVideo video;

    public String getVideoUrl() {
        if (video == null) {
            return "";
        }
        return video.base;
    }

    public String getAltVideoUrl() {
        if (video == null) {
            return "";
        }
        return video.webm;
    }
}
