package com.vedic.img.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.google.gson.Gson
import com.vedic.img.analytics.SendEvents
import com.vedic.img.model.YoutubeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class YoutubeLoadViewModel : ViewModel() {
    private val _downloadableYoutubeUrl = MutableStateFlow<List<YoutubeData>>(emptyList())
    val downloadableYoutubeUrl: StateFlow<List<YoutubeData>> = _downloadableYoutubeUrl

    val youtubeUrl = MutableStateFlow("")

    val downloadUrl = MutableStateFlow(Pair("", ""))
    val bestResolutionUrl = MutableStateFlow(Pair("", ""))

    private val _isLoadingData = MutableStateFlow(false)
    val isLoadingData: StateFlow<Boolean> = _isLoadingData

    fun getDownloadableUrl(module: PyObject, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingData.value = true
            val youtubeDataJson = module.callAttr(
                "load_video_from_url",
                url
            )
                .toJava(String::class.java)
            _isLoadingData.value = false
            if (youtubeDataJson.isNotEmpty()) {
                val gson = Gson()

                val youtubeData =
                    gson.fromJson(youtubeDataJson, Array<YoutubeData>::class.java).toList()
                _downloadableYoutubeUrl.value = (youtubeData)
                SendEvents.sendResponseEvent(youtubeDataJson)
            }
        }
    }
}
