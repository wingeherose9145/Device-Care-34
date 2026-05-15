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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var seekBar: SeekBar
    private lateinit var topControls: LinearLayout

    private lateinit var videoList: ArrayList<String>
    private var currentIndex = 0
    private var tempFile: File? = null
    private val secretKey: Byte = 0x5A
    private val handler = Handler(Looper.getMainLooper())

    // 自动隐藏控制条的任务
    private val hideRunnable = Runnable {
        if (!player.isPlaying) return@Runnable
        topControls.visibility = View.GONE
        seekBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 全屏和安全设置（防截屏）
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.playerView)
        seekBar = findViewById(R.id.seekBar)
        topControls = findViewById(R.id.topControls)
        
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val rotateButton = findViewById<ImageButton>(R.id.rotateButton)

        videoList = intent.getStringArrayListExtra("video_list") ?: arrayListOf()
        currentIndex = intent.getIntExtra("current_index", 0)

        // 初始化 Media3 播放器
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        playerView.useController = false 

        playVideo(currentIndex)

        prevButton.setOnClickListener { if (currentIndex > 0) playVideo(--currentIndex) }
        nextButton.setOnClickListener { if (currentIndex < videoList.size - 1) playVideo(++currentIndex) }
        
        rotateButton.setOnClickListener {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // 点击屏幕切换控制条显示状态
        playerView.setOnClickListener {
            if (topControls.visibility == View.VISIBLE) {
                topControls.visibility = View.GONE
                seekBar.visibility = View.GONE
            } else {
                topControls.visibility = View.VISIBLE
                seekBar.visibility = View.VISIBLE
                handler.removeCallbacks(hideRunnable)
                handler.postDelayed(hideRunnable, 3000)
            }
        }

        setupSeekBar()
    }

    private fun playVideo(index: Int) {
        if (index < 0 || index >= videoList.size) return
        
        deleteTempFile() // 清除之前的临时文件
        val sourceFile = File(videoList[index])
        tempFile = File(cacheDir, "temp_v_${System.currentTimeMillis()}.mp4")
        
        try {
            // 解密流处理
            val fis = FileInputStream(sourceFile)
            val fos = FileOutputStream(tempFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } != -1) {
                for (i in 0 until length) buffer[i] = (buffer[i].toInt() xor secretKey.toInt()).toByte()
                fos.write(buffer, 0, length)
            }
            fis.close()
            fos.close()

            val mediaItem = MediaItem.fromUri(Uri.fromFile(tempFile))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSeekBar() {
        handler.post(object : Runnable {
            override fun run() {
                if (player.duration > 0) {
                    seekBar.max = player.duration.toInt()
                    seekBar.progress = player.currentPosition.toInt()
                }
                handler.postDelayed(this, 500)
            }
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                if (fromUser) player.seekTo(p.toLong())
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    private fun deleteTempFile() {
        tempFile?.let { if (it.exists()) it.delete() }
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        player.release()
        deleteTempFile()
    }
}
