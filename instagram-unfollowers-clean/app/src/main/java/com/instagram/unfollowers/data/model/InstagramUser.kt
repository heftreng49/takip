package com.instagram.unfollowers.data.model

data class InstagramUser(
    val username: String,
    val fullName: String = "",
    val profileUrl: String = "",
    val timestamp: Long = 0L
)

data class AnalysisResult(
    val following: List<InstagramUser>,
    val followers: List<InstagramUser>,
    val unfollowers: List<InstagramUser>,      // Following but not following back
    val notFollowingBack: List<InstagramUser>  // Followers that you don't follow
)

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val result: AnalysisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
