package com.vedic.img.model

data class YoutubeData(
    val title: String,
    val download_url: String,
    val thumb_nail: String,
    val name: String,
    val resolution: String,
    val best_resolution_video_only: String
) {
    override fun toString(): String {
        return "name:$name\nresolution:$resolution"
    }
}
