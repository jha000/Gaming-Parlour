package com.app.gamingparlour.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.app.gamingparlour.R

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val i = Intent(
                this@splashScreen,
                MainActivity::class.java
            )
            startActivity(i)
            finish()
        }, SPLASH_SCREEN_TIME_OUT.toLong())

    }

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT = 3000
    }

}