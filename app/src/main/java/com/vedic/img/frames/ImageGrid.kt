package com.vedic.img.frames

import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.vedic.img.ContentWithProgressBar


@Composable
fun ImageGrid() {
    val viewModel = viewModel<FrameListViewModel>()
    val liveDataValue by viewModel.imageUrls.collectAsState()
    val isLoadingState by viewModel.isLoadingData.collectAsState()

    if (!isLoadingState) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp)
        ) {
            items(liveDataValue.size) { image ->
                val imageModifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1.3f)
                    .clip(RoundedCornerShape(16.dp))
                AsyncImage(
                    model = liveDataValue,
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
        }
    }
    ContentWithProgressBar(isLoading = isLoadingState)
}

