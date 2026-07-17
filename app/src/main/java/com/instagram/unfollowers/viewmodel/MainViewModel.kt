package com.instagram.unfollowers.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instagram.unfollowers.data.model.AnalysisResult
import com.instagram.unfollowers.data.model.AnalysisState
import com.instagram.unfollowers.data.model.InstagramUser
import com.instagram.unfollowers.data.parser.InstagramDataParser
import com.instagram.unfollowers.data.parser.ParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.zip.ZipInputStream

class MainViewModel : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedTab(tab: Int) { _selectedTab.value = tab; _searchQuery.value = "" }

    fun processZipFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                val result = withContext(Dispatchers.IO) { parseZipFromUri(context, uri) }
                _analysisState.value = AnalysisState.Success(result)
            } catch (e: ParseException) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Bilinmeyen hata")
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error("Dosya işlenemedi: ${e.message ?: "Bilinmeyen hata"}")
            }
        }
    }

    fun processJsonFiles(context: Context, followersUri: Uri, followingUri: Uri) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    val followersJson = context.contentResolver.openInputStream(followersUri)
                        ?.bufferedReader()?.readText() ?: throw ParseException("Followers dosyası okunamadı")
                    val followingJson = context.contentResolver.openInputStream(followingUri)
                        ?.bufferedReader()?.readText() ?: throw ParseException("Following dosyası okunamadı")
                    val followers = InstagramDataParser.parseFollowers(followersJson)
                    val following = InstagramDataParser.parseFollowing(followingJson)
                    buildResult(following, followers)
                }
                _analysisState.value = AnalysisState.Success(result)
            } catch (e: ParseException) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Bilinmeyen hata")
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error("Dosya işlenemedi: ${e.message ?: "Bilinmeyen hata"}")
            }
        }
    }

    private fun parseZipFromUri(context: Context, uri: Uri): AnalysisResult {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw ParseException("ZIP dosyası açılamadı")

        val followersJsonList = mutableListOf<String>()
        var followingJson: String? = null

        ZipInputStream(inputStream.buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val fileName = entry.name.substringAfterLast("/").lowercase()
                when {
                    fileName == "following.json" && followingJson == null -> {
                        followingJson = zip.readBytes().toString(Charsets.UTF_8)
                    }
                    fileName.startsWith("followers") && fileName.endsWith(".json") && !fileName.contains("following") -> {
                        followersJsonList.add(zip.readBytes().toString(Charsets.UTF_8))
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        if (followersJsonList.isEmpty()) throw ParseException(
            "ZIP içinde followers_1.json bulunamadı.\nLütfen Instagram'dan indirdiğiniz ZIP dosyasını yükleyin."
        )
        if (followingJson == null) throw ParseException(
            "ZIP içinde following.json bulunamadı.\nLütfen Instagram'dan indirdiğiniz ZIP dosyasını yükleyin."
        )

        val followers = followersJsonList.flatMap {
            InstagramDataParser.parseFollowers(it)
        }.distinctBy { it.username }

        val following = InstagramDataParser.parseFollowing(followingJson!!)
        return buildResult(following, followers)
    }

    private fun buildResult(following: List<InstagramUser>, followers: List<InstagramUser>): AnalysisResult {
        val (unfollowers, notFollowingBack) = InstagramDataParser.analyze(following, followers)
        return AnalysisResult(following = following, followers = followers, unfollowers = unfollowers, notFollowingBack = notFollowingBack)
    }

    fun filteredList(list: List<InstagramUser>): List<InstagramUser> {
        val q = _searchQuery.value.trim().lowercase()
        return if (q.isEmpty()) list else list.filter { it.username.lowercase().contains(q) }
    }

    fun reset() { _analysisState.value = AnalysisState.Idle; _searchQuery.value = ""; _selectedTab.value = 0 }
}
