package com.byoutline.kickmaterial.model;

import android.text.TextUtils;
import com.byoutline.kickmaterial.utils.DateTimeParcelConverter;
import com.byoutline.kickmaterial.utils.PeriodToStringConverter;
import com.byoutline.kickmaterial.utils.QueryParamsExtractor;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.text.DecimalFormat;
import java.util.Map;


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
@Parcel(Parcel.Serialization.FIELD)
public class Project {

    public static final DecimalFormat MONEY_USA_FORMATTER = new DecimalFormat("###,###,###,###");

    public int id;
    @SerializedName("name")
    public String title;
    @SerializedName("blurb")
    public String desc;
    @SerializedName("goal")
    public float totalAmount;
    @SerializedName("pledged")
    public float gatheredAmount;
    @SerializedName("backers_count")
    public int backers;

    public ProjectCreator creator;

    //    public String photoUrl = "http://xe.cdn03.imgwykop.pl/c3397993/link_ERJss0RLvG26DPiTOgxUNjALqUU469qu,w300h223.jpg";
    public ProjectPhoto photo;
    public String currencySymbol;
    @ParcelPropertyConverter(DateTimeParcelConverter.class)
    public DateTime launchedAt;
    @ParcelPropertyConverter(DateTimeParcelConverter.class)
    public DateTime deadline;
    public ProjectUrls urls;

    public String getPhotoUrl() {
        return photo.med;
    }

    public String getBigPhotoUrl() {
        // Currently full photos have size of w=560&h=420.
        return photo.full;
    }

    public String getAuthorUrl() {
        if (creator == null) {
            return "";
        }
        return creator.urls.web.user;
    }

    public ProjectTime getTimeLeft() {
        return getTimeLeft(DateTime.now());
    }

    ProjectTime getTimeLeft(DateTime now) {
        if (now.isAfter(deadline)) {
            // TODO: add successful.
            return new ProjectTime("ENDED", deadline.toString());
        }
        Period period = new Period(now, deadline, PeriodType.dayTime());
        return PeriodToStringConverter.periodToProjectTime(period);
    }


    public float getPercentProgress() {
        if (gatheredAmount == 0) {
            return 0;
        }
        return Math.min(1f, gatheredAmount / totalAmount) * 100;
    }

    public boolean isFunded() {
        return gatheredAmount >= totalAmount;
    }

    public String getProjectName() {
        return title;
    }

    public String getGatheredAmount() {
        return MONEY_USA_FORMATTER.format(gatheredAmount);
    }

    public String getTotalAmount() {
        return MONEY_USA_FORMATTER.format(totalAmount);
    }

    public Map<String, String> getDetailsQueryMap() {
        return QueryParamsExtractor.getQueryParams(urls.api.project);
    }

    public String getProjectUrl() {
        if (urls == null || urls.web == null) {
            return "";
        }
        return urls.web.project;
    }

    public String getPledgeUrl() {
        return getGeneratedUrl("/pledge/new?clicked_reward=false");
    }

    public String getCommentsUrl() {
        return getGeneratedUrl("/comments");
    }

    public String getUpdatesUrl() {
        return getGeneratedUrl("/updates");
    }

    private String getGeneratedUrl(String suffix) {
        String projectUlr = getProjectUrl();
        if (TextUtils.isEmpty(projectUlr)) {
            return "";
        }
        return projectUlr + suffix;
    }


    public String getProjectCreatorAvatar() {
        String avatarUrl = "";

        if (creator != null && creator.avatar != null) {
            avatarUrl = creator.avatar.medium;
        }

        return avatarUrl;
    }

    public String getAuthorName() {
        String name = "";

        if (creator != null) {
            name = creator.name;
        }
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return id == project.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", totalAmount=" + totalAmount +
                ", gatheredAmount=" + gatheredAmount +
                ", backers=" + backers +
                ", creator=" + creator +
                ", photo=" + photo +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", launchedAt=" + launchedAt +
                ", deadline=" + deadline +
                ", urls=" + urls +
                '}';
    }
}
