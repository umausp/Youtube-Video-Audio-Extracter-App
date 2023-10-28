package com.vedic.img.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.vedic.img.model.YoutubeData

object SendEvents {

    fun sendSearchClickEvent(text: String, clickFrom: String) {
        val searchClickEvent = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, text)
            putString("clickFrom", clickFrom)
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SEARCH, searchClickEvent)
    }

    fun sendSearchClearClickEvent() {
        val bundle = Bundle()
        Firebase.analytics.logEvent("search_clear_button_click", bundle)
    }

    fun sendDownloadClickEvent(data: YoutubeData) {
        val searchClickEvent = Bundle().apply {
            putString(FirebaseAnalytics.Param.TERM, data.toString())
        }
        Firebase.analytics.logEvent("download_clicked", searchClickEvent)
    }

    fun sendResponseEvent(data: String) {
        val searchClickEvent = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEMS, data)
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, searchClickEvent)
    }

    fun sendWatchClickEvent(data: YoutubeData, clickedFrom: String) {
        val searchClickEvent = Bundle().apply {
            putString(FirebaseAnalytics.Param.TERM, data.toString())
            putString("clickedFrom", clickedFrom)
        }
        Firebase.analytics.logEvent("watch_clicked", searchClickEvent)
    }

    fun sendVideoVisibleEvent(name: String) {
        val searchClickEvent = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        }
        Firebase.analytics.logEvent("video_visible", searchClickEvent)
    }

    fun sendNotificationAllowedEvent(isAllowed: Boolean) {
        val searchClickEvent = Bundle().apply {
            putBoolean("is_allowed_by_user", isAllowed)
        }
        Firebase.analytics.logEvent("push_opted", searchClickEvent)
    }

}