package com.app.gamingparlour.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.app.gamingparlour.R
import com.google.firebase.database.FirebaseDatabase

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        window.statusBarColor = resources.getColor(R.color.black);

        val name = findViewById<EditText>(R.id.name)
        val username = findViewById<EditText>(R.id.username)
        val game = findViewById<EditText>(R.id.game)
        val channel = findViewById<EditText>(R.id.channel)
        val save = findViewById<Button>(R.id.save)

        val sharedPreferences = getSharedPreferences("myKey", Context.MODE_PRIVATE)

        val nameValue = sharedPreferences.getString("name", "")
        val usernameValue = sharedPreferences.getString("username", "")
        val gameValue = sharedPreferences.getString("game", "")
        val channelValue = sharedPreferences.getString("channel", "")

        name.setText(nameValue)
        username.setText(usernameValue)
        game.setText(gameValue)
        channel.setText(channelValue)


        save.setOnClickListener {
            val nameText = name.text.toString()
            val usernameText = username.text.toString()
            val gameText = game.text.toString()
            val channelText = channel.text.toString()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            name.error = if (nameText.isBlank()) "Name is required" else null
            username.error = if (usernameText.isBlank()) "Username is required" else null
            game.error = if (gameText.isBlank()) "Game is required" else null

            if (nameText.isBlank() || usernameText.isBlank() || gameText.isBlank()) {
                Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            } else {

                val userReference = FirebaseDatabase.getInstance().reference.child("users").child(usernameText)

                userReference.child("name").setValue(nameText)
                userReference.child("game").setValue(gameText)
                userReference.child("channel").setValue(channel.text.toString())

                sharedPreferences.edit().apply {
                    putString("name", nameText)
                    putString("username", usernameText)
                    putString("game", gameText)
                    putString("channel", channelText)
                    apply()
                }
                    val i = Intent(
                        this@Registration,
                        Dashboard::class.java
                    )
                    startActivity(i)

            }
        }
    }
}
