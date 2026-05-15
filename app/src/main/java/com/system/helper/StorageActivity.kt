package com.system.helper

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class StorageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_storage)

        val imageText =
            findViewById<TextView>(
                R.id.imageText
            )

        val videoText =
            findViewById<TextView>(
                R.id.videoText
            )

        val apkText =
            findViewById<TextView>(
                R.id.apkText
            )

        val cacheText =
            findViewById<TextView>(
                R.id.cacheText
            )

        val scanButton =
            findViewById<Button>(
                R.id.scanButton
            )

        imageText.text =
            "${Random.nextInt(2, 8)}.${Random.nextInt(1,9)} GB"

        videoText.text =
            "${Random.nextInt(8, 30)}.${Random.nextInt(1,9)} GB"

        apkText.text =
            "${Random.nextInt(1, 4)}.${Random.nextInt(1,9)} GB"

        cacheText.text =
            "${Random.nextInt(1, 5)}.${Random.nextInt(1,9)} GB"

        scanButton.setOnClickListener {

            Toast.makeText(
                this,
                "Storage Optimized",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
