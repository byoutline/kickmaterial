package com.byoutline.kickmaterial.model

import paperparcel.PaperParcel

@PaperParcel
class ProjectDetails : Project() {

    var rewards: List<Reward>? = null
    var commentsCount: Int = 0
    var updatesCount: Int = 0
    var video: ProjectVideo? = null

    val videoUrl: String
        get() = video?.base ?: ""


    val altVideoUrl: String
        get() = video?.webm ?: ""

    companion object {
        @JvmField val CREATOR = PaperParcelProjectDetails.CREATOR
    }
}
