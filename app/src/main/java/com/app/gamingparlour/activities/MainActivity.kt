package com.app.gamingparlour.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.gamingparlour.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val i = Intent(this@MainActivity, Dashboard::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        val sendOTP=findViewById<Button>(R.id.sendOTP)
        val phone=findViewById<EditText>(R.id.phone)
        val progressBar = findViewById<ProgressBar>(R.id.progressbarforotp)

        val sharedPreferences = getSharedPreferences("myKey", Context.MODE_PRIVATE)

        sharedPreferences.edit().apply {
            putString("mobile", phone.text.toString())
            apply()
        }

        sendOTP.setOnClickListener {

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val phoneNumber = phone.text.toString()
            if (isValidPhoneNumber(phoneNumber)) {
                progressBar.visibility=View.VISIBLE
                sendOTP.visibility=View.GONE
                val countryCode = "+91"
                val formattedPhoneNumber = "$countryCode$phoneNumber"
                sendVerificationCode(formattedPhoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Pattern.compile("^[0-9]{10}\$")
        return pattern.matcher(phoneNumber).matches()
    }

    private fun sendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            java.util.concurrent.TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    val intent = Intent(this@MainActivity, otp::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Toast.makeText(
                        this@MainActivity,
                        "Verification failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

}