package com.vedic.img.frames

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ExtractFrameFromVideo {

    suspend fun downloadVideo(context: Context, videoUrl: String, videoFileName: String) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(videoUrl)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val videoBytes = response.body?.bytes()

                    if (videoBytes != null) {
                        saveVideo(videoBytes, context, videoFileName)
//                        val directory = File(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_DOWNLOADS), "YourAppFolder")
//                        directory.mkdirs()
//
//                        val videoFile = File(directory, videoFileName)
//
//                        FileOutputStream(videoFile).use { output ->
//                            output.write(videoBytes)
//                        }
                    } else {
                        Toast.makeText(context, "Failed to download video", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "Failed to download video", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to download video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveVideo(videoByteArray: ByteArray, context: Context, name: String) {
        val videoFile = File(context.cacheDir, name)
        val outputDirectory = File(context.cacheDir, "frames")

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(videoFile).use { outputStream ->
                    outputStream.write(videoByteArray)
                }
            }
            Log.d("logs", "Video downloaded to $videoFile")
//                Toast.makeText(
//                    context,
//                    "Video downloaded to $videoFile",
//                    Toast.LENGTH_SHORT
//                ).show()
            extractFramesFromVideo(videoFile, outputDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun extractFramesFromVideo(videoFile: File, outputDirectory: File) {
    val mediaMetadataRetriever = MediaMetadataRetriever()

    // Set the data source to the video file
    mediaMetadataRetriever.setDataSource(videoFile.path)

    // Get the video duration in microseconds
    val durationUs =
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong() ?: 0

    // Define the frame interval (adjust as needed)
    val frameIntervalUs = 1000000L  // 1 frame per second

    // Initialize frame time
    var frameTimeUs: Long = 0

    // Loop to extract frames
    while (frameTimeUs < durationUs) {
        val frameBitmap = mediaMetadataRetriever.getFrameAtTime(
            frameTimeUs,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )

        if (frameBitmap != null) {
            // Save the frame as an image file
            val frameFilename = File(outputDirectory, "frame_${frameTimeUs / 1000}.jpg")
            saveBitmapAsImage(frameBitmap, frameFilename)
            Log.d("saved", frameFilename.absolutePath)
        }

        frameTimeUs += frameIntervalUs
    }
}

private fun saveBitmapAsImage(bitmap: Bitmap, file: File) {
    try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
