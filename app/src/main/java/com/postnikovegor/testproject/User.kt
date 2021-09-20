package com.postnikovegor.testproject

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val avatar: String,
    @SerializedName("badge_title") val badgeTitle: String,
    val bio: String,
    val fullname: String,
    val gamification: GamificationData,
    @SerializedName("invitation_code") val invitationCode: String,
    @SerializedName("comments_posted") val postedData: PostedData,
    val username: String,
    @SerializedName("selected_tracks") val selectedTracks: List<Int>,
    @SerializedName("completed_tracks") val completedTracks: List<Int>,
    val country: String?,
    val languages: List<String>,
    val experience: String,
    @SerializedName("github_username") val githubUsername: String,
    @SerializedName("linkedin_username") val linkedinUsername: String,
    @SerializedName("twitter_username") val twitterUsername: String,
    @SerializedName("reddit_username") val redditUsername: String,
    @SerializedName("facebook_username") val facebookUsername: String,
    @SerializedName("discord_id") val discordId: Int
) {

    data class PostedData(
        val comment: Int,
        val hint: Int,
        @SerializedName("useful link") val usefulLink: Int,
        val solutions: Int
    )

    data class GamificationData(
        @SerializedName("active_days") val activeDays: Int,
        @SerializedName("daily_step_completed_count") val dailyStepCompletedCount: Int,
        @SerializedName("passed_problems") val passedProblems: Int,
        @SerializedName("passed_projects") val passedProjects: Int,
        @SerializedName("passed_topics") val passedTopics: Int,
        @SerializedName("progress_updated_at") val progressUpdatedAt: String
    )
}