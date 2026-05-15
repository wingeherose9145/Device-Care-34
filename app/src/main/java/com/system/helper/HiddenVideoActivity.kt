package com.system.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class HiddenVideoActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var adapter: ArrayAdapter<String>

    private val videoFiles = ArrayList<File>()

    private val videoNames = ArrayList<String>()

    private val secretKey: Byte = 0x5A

    private val pickVideo =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.isNotEmpty()) {

                for (uri in uris) {

                    importEncryptedVideo(uri)
                }

                loadVideos()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_hidden_video)

        listView =
            findViewById(R.id.videoListView)

        val addButton =
            findViewById<ImageButton>(
                R.id.addButton
            )

        adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                videoNames
            )

        listView.adapter = adapter

        loadVideos()

        addButton.setOnClickListener {

            pickVideo.launch("video/*")
        }

        listView.setOnItemClickListener { _, _, position, _ ->

            val intent =
                Intent(
                    this,
                    PlayerActivity::class.java
                )

            val paths = ArrayList<String>()

            for (file in videoFiles) {

                paths.add(file.absolutePath)
            }

            intent.putStringArrayListExtra(
                "videoList",
                paths
            )

            intent.putExtra(
                "currentIndex",
                position
            )

            startActivity(intent)
        }

        listView.setOnItemLongClickListener {
                _, _, position, _ ->

            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete this video?")
                .setPositiveButton("Delete") {
                        _, _ ->

                    videoFiles[position].delete()

                    loadVideos()
                }
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .show()

            true
        }
    }

    private fun importEncryptedVideo(
        uri: Uri
    ) {

        try {

            val inputStream =
                contentResolver
                    .openInputStream(uri)
                    ?: return

            val randomName =
                UUID.randomUUID()
                    .toString() + ".dat"

            val outputFile =
                File(filesDir, randomName)

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

            Toast.makeText(
                this,
                "Imported",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Import Failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadVideos() {

        videoFiles.clear()

        videoNames.clear()

        val files =
            filesDir.listFiles()

        if (files != null) {

            for (file in files) {

                if (file.extension == "dat") {

                    videoFiles.add(file)

                    videoNames.add(
                        "Video ${
                            videoNames.size + 1
                        }"
                    )
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}
