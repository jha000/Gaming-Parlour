package com.app.gamingparlour.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.app.gamingparlour.R
import com.app.gamingparlour.activities.MainActivity
import com.app.gamingparlour.activities.Registration
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream


class profileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val name = view.findViewById<TextView>(R.id.name1)
        val username = view.findViewById<TextView>(R.id.username1)
        val game = view.findViewById<TextView>(R.id.game1)
        val channel = view.findViewById<TextView>(R.id.channel1)
        val location = view.findViewById<TextView>(R.id.location)

        val editProfile = view.findViewById<Button>(R.id.editProfile)
        val fab = view.findViewById<ImageView>(R.id.profile_image)

        editProfile.setOnClickListener{
            val intent = Intent(requireActivity(), Registration::class.java)
            startActivity(intent)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)

        val nameValue = sharedPreferences.getString("name", "")
        val usernameValue = sharedPreferences.getString("username", "")
        val gameValue = sharedPreferences.getString("game", "")
        val channelValue = sharedPreferences.getString("channel", "")
        val locationValue = sharedPreferences.getString("address", "")

        name.text = nameValue
        username.text = usernameValue
        game.text = gameValue
        channel.text = channelValue
        location.text = locationValue

        if (channelValue.isNullOrBlank()) {
            channel.text= "Channel name"
        } else {
            channel.text = channelValue
        }

        val logout = view.findViewById<TextView>(R.id.logout)
        logout.setOnClickListener {
                val user = FirebaseAuth.getInstance()
                user.signOut()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Logout Successful",
                    Toast.LENGTH_SHORT
                ).show()

        }

        fab.setOnClickListener {
            ImagePicker.with(this@profileFragment)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }

        val base64: String? = sharedPreferences.getString("image", null)
        if (base64 != null && base64.isNotEmpty()) {
            val byteArray = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            fab.setImageBitmap(bitmap)
        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImg = data!!.data
        val cover = view?.findViewById<View>(R.id.profile_image) as ImageView
        cover.setImageURI(selectedImg)

        val bitmap = (cover.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val sharedPreferences = requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("image", base64)
        editor.apply()
    }
}