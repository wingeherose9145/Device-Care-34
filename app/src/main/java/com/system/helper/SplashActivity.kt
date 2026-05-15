package com.system.helper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_splash
        )

       val root =
    findViewById<android.view.View>(
        android.R.id.content
    )

root.alpha = 0f

root.animate()
    .alpha(1f)
    .setDuration(700)
    .start()
        
        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            startActivity(
                Intent(
                    this,
                    FakeHomeActivity::class.java
                )
            )

            finish()

        }, 1500)
    }
}
