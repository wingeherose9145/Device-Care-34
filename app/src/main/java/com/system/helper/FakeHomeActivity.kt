package com.system.helper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FakeHomeActivity : AppCompatActivity() {

    private var clickCount = 0

    private val handler =
        Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_fake_home
        )

        val titleText =
            findViewById<TextView>(
                R.id.titleText
            )

        val batteryText =
            findViewById<TextView>(
                R.id.batteryText
            )

        val tempText =
            findViewById<TextView>(
                R.id.tempText
            )

        val ramText =
            findViewById<TextView>(
                R.id.ramText
            )

        val cacheText =
            findViewById<TextView>(
                R.id.cacheText
            )

        val cleanButton =
            findViewById<Button>(
                R.id.cleanButton
            )

        val storageLayout =
            findViewById<LinearLayout>(
                R.id.storageLayout
            )

        val optimizeButton =
            findViewById<Button>(
                R.id.optimizeButton
            )

        val randomTemp =
            (31..36).random()

        val randomRam =
            (48..62).random() / 10.0

        val randomCache =
            (12..24).random() / 10.0

        batteryText.text =
            "Excellent"

        tempText.text =
            "Temperature: ${randomTemp}°C"

        ramText.text =
            "${randomRam}GB / 8GB Used"

        cacheText.text =
            "Junk Cache: ${randomCache}GB"

        titleText.setOnClickListener {

            clickCount++

            if (clickCount >= 3) {

                clickCount = 0

                showPasswordDialog()
            }

            handler.removeCallbacksAndMessages(null)

            handler.postDelayed({

                clickCount = 0

            }, 1000)
        }

        cleanButton.setOnClickListener {

            simulateCleaning()
        }

        storageLayout.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StorageActivity::class.java
                )
            )
        }

        optimizeButton.setOnClickListener {

            fakeOptimize()
        }
    }

    private fun showPasswordDialog() {

        val input =
            EditText(this)

        input.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(this)
            .setTitle("Enter Password")
            .setView(input)
            .setPositiveButton("Unlock") { _, _ ->

                val password =
                    input.text.toString()

                if (password == "1234") {

                    startActivity(
                        Intent(
                            this,
                            HiddenVideoActivity::class.java
                        )
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Wrong Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun simulateCleaning() {

        Toast.makeText(
            this,
            "Cleaning completed",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun fakeOptimize() {

        val loadingText =
            TextView(this)

        loadingText.text =
            "Optimizing system resources..."

        loadingText.textSize = 16f

        loadingText.setPadding(
            0,
            30,
            0,
            0
        )

        val progressBar =
            ProgressBar(this)

        progressBar.isIndeterminate = true

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            60,
            40,
            60,
            40
        )

        layout.addView(progressBar)
        layout.addView(loadingText)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Smart Optimize")
                .setView(layout)
                .setCancelable(false)
                .create()

        dialog.show()

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            dialog.dismiss()

            Toast.makeText(
                this,
                "System optimization completed",
                Toast.LENGTH_SHORT
            ).show()

        }, 2500)
    }
}
