package com.system.helper

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val playerView =
            findViewById<PlayerView>(
                R.id.playerView
            )

        player =
            ExoPlayer.Builder(this).build()

        playerView.player = player

        val videoList =
            intent.getStringArrayListExtra(
                "video_list"
            )

        if (
            videoList == null ||
            videoList.isEmpty()
        ) {
            finish()
            return
        }

        val videoFile =
            File(videoList[0])

        val mediaItem =
            MediaItem.fromUri(
                Uri.fromFile(videoFile)
            )

        player.setMediaItem(mediaItem)

        player.prepare()

        player.play()
    }

    override fun onDestroy() {

        super.onDestroy()

        player.release()
    }
}
