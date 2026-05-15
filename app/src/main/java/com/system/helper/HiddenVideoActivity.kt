package com.system.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class HiddenVideoActivity : AppCompatActivity() {

    private lateinit var videoListView: ListView

    private val realVideoPaths =
        mutableListOf<String>()

    private val displayNames =
        mutableListOf<String>()

    private lateinit var adapter:
            ArrayAdapter<String>

    private val pickVideoLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            if (uris.isNotEmpty()) {

                for (uri in uris) {

                    saveVideoToInternalStorage(uri)
                }

                loadVideos()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_hidden_video
        )

        val addButton =
            findViewById<FloatingActionButton>(
                R.id.addButton
            )

        videoListView =
            findViewById(
                R.id.videoListView
            )

        adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                displayNames
            )

        videoListView.adapter =
            adapter

        addButton.setOnClickListener {

            pickVideoLauncher.launch(
                arrayOf("video/*")
            )
        }

        videoListView.setOnItemClickListener {
                _, _, position, _ ->

            val intent =
                Intent(
                    this,
                    PlayerActivity::class.java
                )

            intent.putStringArrayListExtra(
                "video_list",
                ArrayList(realVideoPaths)
            )

            intent.putExtra(
                "video_index",
                position
            )

            startActivity(intent)
        }

        loadVideos()
    }

    private fun loadVideos() {

        realVideoPaths.clear()

        displayNames.clear()

        val hiddenDir =
            File(
                filesDir,
                "hidden_videos"
            )

        if (!hiddenDir.exists()) {

            hiddenDir.mkdirs()
        }

        val files =
            hiddenDir.listFiles()

        files?.forEach { file ->

            realVideoPaths.add(
                file.absolutePath
            )

            val displayName =
                if (file.name.contains("__")) {

                    file.name.substringAfter("__")

                } else {

                    file.name
                }

            displayNames.add(
                displayName
            )
        }

        adapter.notifyDataSetChanged()
    }

    private fun saveVideoToInternalStorage(
        uri: Uri
    ) {

        var originalName =
            "video.mp4"

        val cursor =
            contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (it.moveToFirst()) {

                originalName =
                    it.getString(nameIndex)
            }
        }

        val randomName =
            UUID.randomUUID()
                .toString()
                .replace("-", "")
                .take(12)

        val finalName =
            "${randomName}__${originalName}"

        val hiddenDir =
            File(
                filesDir,
                "hidden_videos"
            )

        if (!hiddenDir.exists()) {

            hiddenDir.mkdirs()
        }

        val outputFile =
            File(
                hiddenDir,
                finalName
            )

        contentResolver
            .openInputStream(uri)
            ?.use { input ->

                FileOutputStream(outputFile)
                    .use { output ->

                        input.copyTo(output)
                    }
            }
    }
}
