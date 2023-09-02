package com.app.gamingparlour.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.app.gamingparlour.R
import com.chaos.view.PinView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class otp : AppCompatActivity() {

    private lateinit var verificationId: String
    private lateinit var lottie: LottieAnimationView
    private lateinit var layoutOtp: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        window.statusBarColor = resources.getColor(R.color.black);

        val submitOTP = findViewById<Button>(R.id.submitOTP)
        val pinview = findViewById<PinView>(R.id.pinview)
        val progressbar = findViewById<ProgressBar>(R.id.progressbar)
        layoutOtp = findViewById(R.id.layoutOtp)

        lottie = findViewById(R.id.lottie)

        submitOTP.setOnClickListener {

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            progressbar.visibility = View.VISIBLE
            submitOTP.visibility = View.GONE
            val enteredOtp = pinview.text.toString()
            verifyOTPWithFirebase(enteredOtp)
        }

        verificationId = intent.getStringExtra("verificationId") ?: ""

    }

    private fun verifyOTPWithFirebase(enteredOtp: String) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationId, enteredOtp)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    layoutOtp.visibility = View.GONE
                    lottie.visibility = View.VISIBLE

                    Handler().postDelayed({
                        val i = Intent(
                            this@otp,
                            Registration::class.java
                        )
                        startActivity(i)
                        finish()
                    }, SPLASH_SCREEN_TIME_OUT.toLong())

                } else {
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT = 2000
    }

}