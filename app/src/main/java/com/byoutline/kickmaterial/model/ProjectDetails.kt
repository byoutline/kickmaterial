package com.byoutline.kickmaterial.model

import paperparcel.PaperParcel

@PaperParcel
class ProjectDetails(
        val rewards: List<Reward>?,
        val commentsCount: Int,
        val updatesCount: Int,
        val video: ProjectVideo?) : Project() {

    val videoUrl: String
        get() = video?.base ?: ""


    val altVideoUrl: String
        get() = video?.webm ?: ""

    companion object {
        @JvmField
        val CREATOR = PaperParcelProjectDetails.CREATOR
    }
}
