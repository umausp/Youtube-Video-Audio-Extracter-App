package com.vedic.img.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.vedic.img.MainActivity
import com.vedic.img.R

class AudioPlayerService : Service() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var handlerThread: HandlerThread
    private lateinit var serviceHandler: Handler
    private var notifications: Notification? = null
    private val NOTIFICATION_ID = 1


    override fun onCreate() {
        super.onCreate()
        handlerThread = HandlerThread("MyServiceThread")
        handlerThread.start()

        // Get the Looper associated with the HandlerThread
        serviceHandler = Handler(handlerThread.looper)
        exoPlayer = ExoPlayer.Builder(this)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_ACTION") {
            exoPlayer?.stop()
            exoPlayer?.release()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            val audioUrl = intent?.getStringExtra("audioUrl")
            val name = intent?.getStringExtra("name")
            val youtubeUri = intent?.getStringExtra("youtubeUri")
            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.stop()
            }

            if (exoPlayer?.isPlaying == false && audioUrl?.isNotEmpty() == true) {
                val mediaItem = MediaItem.fromUri(audioUrl)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
            notifications = createNotification(name, youtubeUri)
            startForeground(NOTIFICATION_ID, notifications)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(name: String?, youtubeUri: String?): Notification {
        val channel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            "Audio Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val stopIntent = Intent(this, AudioPlayerService::class.java)
        stopIntent.action = "STOP_ACTION" // You can use any unique action string
        val stopPendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.data = Uri.parse(youtubeUri)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle("Now Playing")
            .setContentText(name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.baseline_stop_circle_24, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.stop()
        }
        exoPlayer?.release()

        exoPlayer = null
        handlerThread.quit()
        stopForeground(STOP_FOREGROUND_DETACH)
    }
}
