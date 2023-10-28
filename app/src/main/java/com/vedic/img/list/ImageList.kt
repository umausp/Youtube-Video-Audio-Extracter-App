package com.vedic.img.list

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.res.vectorResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.vedic.img.R
import com.vedic.img.model.YoutubeData
import com.vedic.img.viewmodel.YoutubeLoadViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.vedic.img.ContentWithProgressBar
import com.vedic.img.analytics.SendEvents

@Composable
fun ImageList() {
    val viewModel = viewModel<YoutubeLoadViewModel>()
    val liveDataValue by viewModel.downloadableYoutubeUrl.collectAsState()
    val isLoadingState by viewModel.isLoadingData.collectAsState()
    if (!isLoadingState) {
        LazyColumn {
            items(liveDataValue) { item ->
                ListItemContent(item)
            }
        }
    }
    ContentWithProgressBar(isLoading = isLoadingState)
}

@Composable
fun ListItemContent(item: YoutubeData) {
    val viewModel = viewModel<YoutubeLoadViewModel>()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                viewModel.downloadUrl.value = Pair(item.download_url, item.name)
                SendEvents.sendWatchClickEvent(item, "image_click")
            },
    ) {

        val imageModifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.3f)
            .clip(RoundedCornerShape(16.dp))

        AsyncImage(
            modifier = imageModifier,
            model =  item.thumb_nail,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${item.resolution} - ${item.name}",
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            CenteredImage(painterResource(id = R.drawable.baseline_download_24), "Download") {
                downloadVideo(context, item.download_url, item.title)
                Toast.makeText(context, "Download Started Please wait!", Toast.LENGTH_SHORT).show()
                SendEvents.sendDownloadClickEvent(item)
            }

            Spacer(modifier = Modifier.width(8.dp))

            CenteredImage(painterResource(id = R.drawable.baseline_play_arrow_24), "Watch") {
                viewModel.downloadUrl.value = Pair(item.download_url, item.name)
                SendEvents.sendWatchClickEvent(item, "watch_icon")
            }

            Spacer(modifier = Modifier.width(8.dp))

//            CenteredImage(
//                iconPainter = painterResource(id = R.drawable.baseline_crop_original_24),
//                text = "Take Frame"
//            ) {
//                viewModel.bestResolutionUrl.value = Pair(item.best_resolution_video_only, item.title)
//            }
        }
    }
}

fun openVideoWithIntent(context: Context, videoUri: String?) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUri))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.setPackage("com.android.chrome")
//    intent.setPackage("com.android.chrome") // Specify the Chrome package name


    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Handle the case where no app is available to handle the Intent
        // You can show a message or provide an alternative action.
    }
}

fun downloadVideo(context: Context, videoUrl: String, fileName: String) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val request = DownloadManager.Request(Uri.parse(videoUrl))
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    request.setTitle(fileName)
    request.setDescription("Downloading...")

    val directory = Environment.DIRECTORY_DOWNLOADS
    request.setDestinationInExternalPublicDir(directory, fileName)

    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

    downloadManager.enqueue(request)
}


@Composable
fun CenteredImage(iconPainter: Painter, text: String, onClick: () -> Unit) {
    val color = Color(android.graphics.Color.parseColor("#6750A4"))
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = iconPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp) // Adjust the size of the image
                    .clip(CircleShape),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White,
        )
    }
}