package com.system.helper

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer

    private lateinit var seekBar: SeekBar

    private lateinit var topControls: LinearLayout

    private lateinit var videoList: ArrayList<String>

    private var currentIndex = 0

    private var tempFile: File? = null

    private val secretKey: Byte = 0x5A

    private val handler =
        Handler(Looper.getMainLooper())

    private var isPortrait = false

    private val hideRunnable = Runnable {

        if (!player.isPlaying) return@Runnable

        topControls.visibility = View.GONE

        seekBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        supportActionBar?.hide()

        setContentView(R.layout.activity_player)

        val playerView =
            findViewById<PlayerView>(
                R.id.playerView
            )

        topControls =
            findViewById(R.id.topControls)

        seekBar =
            findViewById(R.id.seekBar)

        val rotateButton =
            findViewById<ImageButton>(
                R.id.rotateButton
            )

        val prevButton =
            findViewById<ImageButton>(
                R.id.prevButton
            )

        val nextButton =
            findViewById<ImageButton>(
                R.id.nextButton
            )

        player = ExoPlayer.Builder(this).build()

        playerView.player = player

        playerView.useController = false

        videoList =
            intent.getStringArrayListExtra(
                "videoList"
            ) ?: arrayListOf()

        currentIndex =
            intent.getIntExtra(
                "currentIndex",
                0
            )

        player.addListener(
            object : Player.Listener {

                override fun onVideoSizeChanged(
                    videoSize: VideoSize
                ) {

                    if (videoSize.height >
                        videoSize.width
                    ) {

                        requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                        isPortrait = true

                    } else {

                        requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                        isPortrait = false
                    }
                }

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {

                    if (playbackState ==
                        Player.STATE_ENDED
                    ) {

                        playNextVideo()
                    }
                }
            })

        playVideo()

        topControls.visibility = View.GONE

        seekBar.visibility = View.GONE

        playerView.setOnClickListener {

            if (player.isPlaying) {

                player.pause()

                topControls.visibility =
                    View.VISIBLE

                seekBar.visibility =
                    View.VISIBLE

            } else {

                player.play()

                startAutoHide()
            }
        }

        rotateButton.setOnClickListener {

            if (isPortrait) {

                requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                isPortrait = false

            } else {

                requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                isPortrait = true
            }
        }

        prevButton.setOnClickListener {

            if (currentIndex > 0) {

                currentIndex--

                playVideo()
            }
        }

        nextButton.setOnClickListener {

            if (currentIndex <
                videoList.size - 1
            ) {

                currentIndex++

                playVideo()
            }
        }

        startSeekBarUpdate()
    }

    private fun playVideo() {

        deleteTempFile()

        val encryptedFile =
            File(videoList[currentIndex])

        tempFile =
            File(
                cacheDir,
                "temp_video.mp4"
            )

        decryptFile(
            encryptedFile,
            tempFile!!
        )

        val mediaItem =
            MediaItem.fromUri(
                Uri.fromFile(tempFile)
            )

        player.setMediaItem(mediaItem)

        player.prepare()

        player.play()

        startAutoHide()
    }

    private fun decryptFile(
        inputFile: File,
        outputFile: File
    ) {

        val inputStream =
            FileInputStream(inputFile)

        val outputStream =
            FileOutputStream(outputFile)

        val buffer =
            ByteArray(4096)

        var length: Int

        while (true) {

            length =
                inputStream.read(buffer)

            if (length == -1) break

            for (i in 0 until length) {

                buffer[i] =
                    (buffer[i].toInt()
                            xor
                            secretKey.toInt())
                        .toByte()
            }

            outputStream.write(
                buffer,
                0,
                length
            )
        }

        inputStream.close()

        outputStream.close()
    }

    private fun deleteTempFile() {

        tempFile?.delete()
    }

    private fun playNextVideo() {

        if (currentIndex <
            videoList.size - 1
        ) {

            currentIndex++

            playVideo()

        } else {

            topControls.visibility =
                View.VISIBLE

            seekBar.visibility =
                View.VISIBLE
        }
    }

    private fun startAutoHide() {

        topControls.visibility =
            View.VISIBLE

        seekBar.visibility =
            View.VISIBLE

        handler.removeCallbacks(hideRunnable)

        handler.postDelayed(
            hideRunnable,
            3000
        )
    }

    private fun startSeekBarUpdate() {

        handler.post(object : Runnable {

            override fun run() {

                if (player.duration > 0) {

                    seekBar.max =
                        player.duration.toInt()

                    seekBar.progress =
                        player.currentPosition.toInt()
                }

                handler.postDelayed(this, 500)
            }
        })

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    if (fromUser) {

                        player.seekTo(
                            progress.toLong()
                        )
                    }
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }
            })
    }

    override fun onPause() {
        super.onPause()

        player.pause()

        topControls.visibility =
            View.VISIBLE

        seekBar.visibility =
            View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()

        deleteTempFile()

        player.release()
    }
}
