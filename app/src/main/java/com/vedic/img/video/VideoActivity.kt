package com.vedic.img.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vedic.img.analytics.SendEvents
import com.vedic.img.list.ImageList
import com.vedic.img.list.SearchYoutubeBox
import com.vedic.img.ui.theme.ImgTheme
import com.vedic.img.viewmodel.YoutubeLoadViewModel

class VideoActivity : ComponentActivity() {
//    private val youtubeViewModel by viewModels<YoutubeLoadViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImgTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    // Check the current orientation
                    val orientation = resources.configuration.orientation

                    // Set the orientation to landscape if not already in landscape
                    if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                    val uri = intent.getStringExtra("uri")
                    val name = intent.getStringExtra("name")
                    if (uri != null) {
                        VideoPlayer(url = uri, name = name)
                        SendEvents.sendVideoVisibleEvent(uri)
                    }
                    with(WindowCompat.getInsetsController(window, window.decorView)) {
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        hide(WindowInsetsCompat.Type.systemBars())
                    }
                }
            }
        }
    }
}