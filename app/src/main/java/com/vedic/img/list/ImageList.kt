package com.vedic.img.list

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.downloadUrl.value = Pair(item.download_url, item.name)
                SendEvents.sendWatchClickEvent(item, "image_click")
            },
    ) {
//
//        AsyncImage(
//            modifier = imageModifier,
//            model = item.thumb_nail,
//            contentDescription = item.name,
//        )

        SubcomposeAsyncImage(
            model = item.thumb_nail,
            contentDescription = item.name,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                CircularProgressIndicator()
            } else {
                SubcomposeAsyncImageContent()
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${item.resolution} - ${item.name}",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

//        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
//            CenteredImage(painterResource(id = R.drawable.baseline_download_24), "Download") {
//                downloadVideo(context, item.download_url, item.title)
//                Toast.makeText(context, "Download Started Please wait!", Toast.LENGTH_SHORT).show()
//                SendEvents.sendDownloadClickEvent(item)
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))

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

            CenteredImage(
                iconPainter = painterResource(id = R.drawable.baseline_audiotrack_24),
                text = "Listen Audio"
            ) {
                viewModel.listenAudio.value = item
            }

            Spacer(modifier = Modifier.width(8.dp))

//            CenteredImage(
//                iconPainter = painterResource(id = R.drawable.baseline_audio_file_24),
//                text = "Only Video"
//            ) {
//                viewModel.bestResolutionUrl.value = Pair(item.best_resolution_video_only, item.name)
//                SendEvents.sendWatchClickEvent(item, "watch_video_only")
//            }
        }
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

    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED or DownloadManager.Request.VISIBILITY_VISIBLE)
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
            fontSize = 16.sp,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}