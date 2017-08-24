package com.byoutline.kickmaterial.model

import android.text.TextUtils
import com.byoutline.kickmaterial.model.utils.PeriodToStringConverter
import com.byoutline.kickmaterial.model.utils.QueryParamsExtractor
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.text.DecimalFormat


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
@PaperParcel
open class Project: PaperParcelable {

    var id: Int = 0
    @SerializedName("name")
    var projectName: String? = null
    @SerializedName("blurb")
    var desc: String? = null
    @SerializedName("goal")
    var totalAmount: Float = 0.toFloat()
    @SerializedName("pledged")
    var gatheredAmount: Float = 0.toFloat()
    @SerializedName("backers_count")
    var backers: Int = 0

    var creator: ProjectCreator? = null

    //    public String photoUrl = "http://xe.cdn03.imgwykop.pl/c3397993/link_ERJss0RLvG26DPiTOgxUNjALqUU469qu,w300h223.jpg";
    var photo: ProjectPhoto? = null
    var currencySymbol: String? = null
    var launchedAt: DateTime? = null
    var deadline: DateTime? = null
    var urls: ProjectUrls? = null

    val photoUrl: String
        get() = photo?.med ?: ""

    // Currently full photos have size of w=560&h=420.
    val bigPhotoUrl: String
        get() = photo?.full ?: ""

    val authorUrl: String
        get() = creator?.urls?.web?.user ?: ""

    fun getTimeLeft(now: DateTime=DateTime.now()): ProjectTime {
        if (now.isAfter(deadline)) {
            // TODO: add successful.
            return ProjectTime("ENDED", deadline!!.toString())
        }
        val period = Period(now, deadline, PeriodType.dayTime())
        return PeriodToStringConverter.periodToProjectTime(period)!!
    }


    val percentProgress: Float
        get() {
            if (gatheredAmount == 0f) {
                return 0f
            }
            return Math.min(1f, gatheredAmount / totalAmount) * 100
        }

    val isFunded: Boolean
        get() = gatheredAmount >= totalAmount

    fun getGatheredAmount(): String {
        return MONEY_USA_FORMATTER.format(gatheredAmount.toDouble())
    }

    fun getTotalAmount(): String {
        return MONEY_USA_FORMATTER.format(totalAmount.toDouble())
    }

    val detailsQueryMap: Map<String, String>
        get() = QueryParamsExtractor.getQueryParams(urls!!.api!!.project!!)

    val projectUrl: String
        get() = urls?.web?.project ?: ""

    val pledgeUrl: String
        get() = getGeneratedUrl("/pledge/new?clicked_reward=false")

    val commentsUrl: String
        get() = getGeneratedUrl("/comments")

    val updatesUrl: String
        get() = getGeneratedUrl("/updates")

    private fun getGeneratedUrl(suffix: String): String {
        val projectUlr = projectUrl
        if (TextUtils.isEmpty(projectUlr)) {
            return ""
        }
        return projectUlr + suffix
    }


    val projectCreatorAvatar: String
        get() = creator?.avatar?.medium ?: ""

    val authorName: String
        get() = creator?.name ?: ""


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        val project = o as? Project ?: return false

        return id == project.id

    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "Project{" +
                "id=" + id +
                ", title='" + projectName + '\'' +
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
                '}'
    }

    companion object {
        val MONEY_USA_FORMATTER = DecimalFormat("###,###,###,###")
        @JvmField val CREATOR = PaperParcelProject.CREATOR
    }
}
