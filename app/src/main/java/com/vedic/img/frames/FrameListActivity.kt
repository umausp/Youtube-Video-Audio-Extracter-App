package com.vedic.img.frames

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.vedic.img.ui.theme.ImgTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FrameListActivity : ComponentActivity() {
    private val frameListViewModel by viewModels<FrameListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImgTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val uri = intent.getStringExtra("uri")
                    val name = intent.getStringExtra("name")
                    callPythonModule(uri)
                    if (uri != null) {
                        ImageGrid()
                    }
                }
            }
        }
    }

    private fun callPythonModule(uri: String?) {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        lifecycleScope.launch {
            val py = Python.getInstance()
            val module = py.getModule("get_highest_resolution_frames")

//            frameListViewModel.videoContent.collectLatest {
//                if (it?.isNotEmpty() == true) {
//                    ExtractFrameFromVideo.downloadVideo(
//                        it,
//                        this@FrameListActivity,
//                        intent.getStringExtra("name") ?: "video_${System.currentTimeMillis()}.mp4"
//                    )
//                }
//            }
            uri?.let {
//                frameListViewModel.getFrames(module = module, it)
                lifecycleScope.launch {
                    ExtractFrameFromVideo.downloadVideo(
                        this@FrameListActivity,
                        it,
                        intent.getStringExtra("name") ?: "video_${System.currentTimeMillis()}.mp4"
                    )
                }
            }
        }

    }
}