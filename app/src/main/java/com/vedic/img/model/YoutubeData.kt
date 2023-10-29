package com.vedic.img.model

data class YoutubeData(
    val title: String,
    val download_url: String,
    val thumb_nail: String,
    val name: String,
    val resolution: String,
    val best_resolution_video_only: String,
    val audio_url: String,
    val youtube_url: String,
    val best_resolution: String
) {
    override fun toString(): String {
        return "name:$name\nresolution:$resolution"
    }
}
