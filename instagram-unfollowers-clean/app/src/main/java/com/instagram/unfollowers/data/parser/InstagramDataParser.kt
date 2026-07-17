package com.instagram.unfollowers.data.parser

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.instagram.unfollowers.data.model.InstagramUser

/**
 * Parses Instagram data export JSON files.
 *
 * Instagram exports two types of files:
 * - followers_1.json   → array of {string_list_data: [{value, href, timestamp}]}
 * - following.json     → {relationships_following: [{string_list_data: [{value, href, timestamp}]}]}
 */
object InstagramDataParser {

    fun parseFollowers(json: String): List<InstagramUser> {
        return try {
            val root = JsonParser.parseString(json)
            // followers_1.json is a JSON array at the root
            if (root.isJsonArray) {
                parseUserArray(root.asJsonArray)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw ParseException("Followers JSON dosyası ayrıştırılamadı: ${e.message}")
        }
    }

    fun parseFollowing(json: String): List<InstagramUser> {
        return try {
            val root = JsonParser.parseString(json)
            when {
                // following.json: {"relationships_following": [...]}
                root.isJsonObject -> {
                    val obj = root.asJsonObject
                    val key = obj.keySet().firstOrNull()
                        ?: throw ParseException("Following JSON dosyası boş veya geçersiz")
                    parseUserArray(obj.getAsJsonArray(key))
                }
                // Also support raw array format
                root.isJsonArray -> parseUserArray(root.asJsonArray)
                else -> throw ParseException("Tanımlanamayan following JSON formatı")
            }
        } catch (e: ParseException) {
            throw e
        } catch (e: Exception) {
            throw ParseException("Following JSON dosyası ayrıştırılamadı: ${e.message}")
        }
    }

    private fun parseUserArray(array: JsonArray): List<InstagramUser> {
        val users = mutableListOf<InstagramUser>()
        for (element in array) {
            try {
                val obj = element.asJsonObject
                val stringListData = obj.getAsJsonArray("string_list_data")
                if (stringListData != null && stringListData.size() > 0) {
                    val data = stringListData[0].asJsonObject
                    val username = data.get("value")?.asString ?: continue
                    val href = data.get("href")?.asString ?: ""
                    val timestamp = data.get("timestamp")?.asLong ?: 0L
                    users.add(
                        InstagramUser(
                            username = username,
                            profileUrl = href,
                            timestamp = timestamp
                        )
                    )
                }
            } catch (_: Exception) {
                // Skip malformed entries
            }
        }
        return users
    }

    fun analyze(
        following: List<InstagramUser>,
        followers: List<InstagramUser>
    ): Pair<List<InstagramUser>, List<InstagramUser>> {
        val followerUsernames = followers.map { it.username }.toSet()
        val followingUsernames = following.map { it.username }.toSet()

        // People you follow but don't follow you back
        val unfollowers = following.filter { it.username !in followerUsernames }

        // People who follow you but you don't follow back
        val notFollowingBack = followers.filter { it.username !in followingUsernames }

        return Pair(unfollowers, notFollowingBack)
    }
}

class ParseException(message: String) : Exception(message)
