package com.vedic.img.frames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FrameListViewModel : ViewModel() {
    private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
    val imageUrls: StateFlow<List<String>> = _imageUrls
    private val _isLoadingData = MutableStateFlow(true)
    val isLoadingData: StateFlow<Boolean> = _isLoadingData

    private val _videoContent = MutableStateFlow<ByteArray?>(null)
    val videoContent: StateFlow<ByteArray?> = _videoContent

    fun getFrames(module: PyObject, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingData.value = true
            val youtubeDataJson = module.callAttr(
                "get_frames_from_url",
                url
            )
                .toJava(ByteArray::class.java)
            _isLoadingData.value = false
            if (youtubeDataJson.isNotEmpty()) {
                _videoContent.value = youtubeDataJson
            }
        }
    }
}