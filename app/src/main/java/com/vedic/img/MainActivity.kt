package com.vedic.img

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.vedic.img.analytics.SendEvents
import com.vedic.img.list.ImageList
import com.vedic.img.list.SearchYoutubeBox
import com.vedic.img.ui.theme.ImgTheme
import com.vedic.img.video.VideoActivity
import com.vedic.img.viewmodel.YoutubeLoadViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val youtubeViewModel by viewModels<YoutubeLoadViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callPythonModule()
        handleIntentUrl()
        setContent {
            ImgTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Column {
                        SearchYoutubeBox()
                        Spacer(modifier = Modifier.width(16.dp))
                        ImageList()
                    }
                }
            }
        }
    }

    private fun handleIntentUrl() {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (sharedText != null) {
                Log.d("intentHandle", sharedText)
                youtubeViewModel.youtubeUrl.value = sharedText
            }
        }
    }

    private fun callPythonModule() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        lifecycleScope.launch {
            val py = Python.getInstance()
            val module = py.getModule("download_youtube_video")
            youtubeViewModel.youtubeUrl.collectLatest {
                if (it.isNotEmpty()) {
                    youtubeViewModel.getDownloadableUrl(module, it)
                }
            }
        }

        lifecycleScope.launch {
            youtubeViewModel.downloadUrl.collectLatest {
                if (it.first.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, VideoActivity::class.java)
                    intent.putExtra("uri", it.first)
                    intent.putExtra("name", it.second)
                    startActivity(intent)
                    youtubeViewModel.downloadUrl.value = Pair("", "")
                }
            }
        }

//        lifecycleScope.launch {
//            youtubeViewModel.bestResolutionUrl.collectLatest {
//                if (it.first.isNotEmpty()) {
////                    val intent = Intent(this@MainActivity, FrameListActivity::class.java)
////                    intent.putExtra("uri", it.first)
////                    intent.putExtra("name", it.second)
////                    startActivity(intent)
////                    youtubeViewModel.bestResolutionUrl.value = Pair("", "")
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        askNotificationPermission()
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            SendEvents.sendNotificationAllowedEvent(true)
        } else {
            SendEvents.sendNotificationAllowedEvent(false)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
