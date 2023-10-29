package com.vedic.img.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File
import java.io.FileOutputStream

class AudioWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val audioUrl = inputData.getString("audioUrl")
        val currentTime = inputData.getLong("currentTime", 0L)
        if (!audioUrl.isNullOrBlank()) {
            val client = OkHttpClient()
            val request = Request.Builder().url(audioUrl).build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val outputFile = File(applicationContext.filesDir, "audio.mp3")
                    val outputStream = FileOutputStream(outputFile)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream?.close()

                    // Now you can play the downloaded audio file with MediaPlayer
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(outputFile.path)
                    mediaPlayer.prepare()
                    mediaPlayer.seekTo(currentTime, MediaPlayer.SEEK_CLOSEST)
                    mediaPlayer.start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Result.success()
    }
}
