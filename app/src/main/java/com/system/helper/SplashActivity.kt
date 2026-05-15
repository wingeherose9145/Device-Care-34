package com.system.helper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 简单的淡入动画
        val root = findViewById<View>(android.R.id.content)
        root.alpha = 0f
        root.animate().alpha(1f).setDuration(700).start()

        // 1.5秒后跳转到伪装主页
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, FakeHomeActivity::class.java))
            finish()
        }, 1500)
    }
}
