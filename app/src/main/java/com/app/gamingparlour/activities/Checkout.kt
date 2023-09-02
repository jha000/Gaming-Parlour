package com.app.gamingparlour.activities

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.app.gamingparlour.R
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject

class Checkout : AppCompatActivity(), PaymentResultListener {

    private lateinit var scrollLayout: ScrollView
    private lateinit var failureLayout: LinearLayout
    private lateinit var successLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        window.statusBarColor = resources.getColor(R.color.black);

        val phoneText = findViewById<TextView>(R.id.phoneText)
        val amount = findViewById<TextView>(R.id.amount)
        val payButton = findViewById<TextView>(R.id.pay)
        val progressbar = findViewById<ProgressBar>(R.id.progressbar)
        val monitor = findViewById<TextView>(R.id.monitor)
        val back = findViewById<ImageView>(R.id.back)
        val tryAgain = findViewById<Button>(R.id.tryAgain)
        val back1 = findViewById<TextView>(R.id.back1)
        val cancel = findViewById<TextView>(R.id.cancel)
        scrollLayout = findViewById(R.id.scrollLayout)
        failureLayout = findViewById(R.id.failureLayout)
        successLayout = findViewById(R.id.successLayout)


        back.setOnClickListener {
            super.onBackPressed()
        }

        back1.setOnClickListener {
            super.onBackPressed()
        }

        cancel.setOnClickListener {
            super.onBackPressed()
        }

        tryAgain.setOnClickListener {
            failureLayout.visibility = View.GONE
            scrollLayout.visibility = View.VISIBLE

            progressbar.visibility = View.GONE
            payButton.visibility = View.VISIBLE
        }

        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")

        val startTimeTextView = findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = findViewById<TextView>(R.id.endTimeTextView)

        startTimeTextView.text = startTime
        endTimeTextView.text = endTime

        val sharedPreferences = getSharedPreferences("myKey", Context.MODE_PRIVATE)

        val Monitorname = sharedPreferences.getString("Monitorname", "")

        monitor.text = Monitorname



        payButton.setOnClickListener {
            progressbar!!.visibility = View.VISIBLE
            payButton.visibility = View.GONE

            val mobileValue = sharedPreferences.getString("mobile", "")

            val samount = amount.text.toString()
            val amountFinal = (samount.toFloat() * 100)

            val checkout = Checkout()

            // set your id as below
            checkout.setKeyID("rzp_live_3B17Ixk1cDL2Zz")

            // set image
            checkout.setImage(R.mipmap.ic_launcher)

            // initialize json object
            val `object` = JSONObject()
            try {
                // to put name
                `object`.put("name", "ZONE 14")

                // put description
                `object`.put("description", "ZONE_14")

                // to set theme color
                `object`.put("theme.color", "#000000")

                // put the currency
                `object`.put("currency", "INR")

                // put amount
                `object`.put("amount", amountFinal)

                // put mobile number
                `object`.put("prefill.contact", mobileValue)

                // put email
                `object`.put("prefill.email", "")

                // open razorpay to checkout activity
                checkout.open(this@Checkout, `object`)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


        val fullText = "Call us +91 111-444-1021 (or) send an email to support team"

        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf("+91")
        val endIndex = fullText.indexOf("21")

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        phoneText.text = spannable

    }

    override fun onPaymentSuccess(p0: String?) {

        scrollLayout.visibility = View.GONE
        successLayout.visibility = View.VISIBLE

        val sharedPreferences = getSharedPreferences("myKey", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("username", "")

        val slotName = findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = findViewById<TextView>(R.id.endTimeTextView)
        val Monitorname = findViewById<TextView>(R.id.monitor)

        val passcode = intent.getStringExtra("passcode")

        val database = FirebaseDatabase.getInstance()
        val monitorsRef = database.getReference("monitors")


        val updates = HashMap<String, Any>()
        updates["status"] = "booked"
        updates["userId"] = userId.toString()

        monitorsRef
            .child(Monitorname.text.toString())
            .child("slots")
            .child(slotName.text.toString())
            .updateChildren(updates)
            .addOnSuccessListener {
                // Successfully updated the data
                // You can perform any additional actions here
            }
            .addOnFailureListener { e ->
                // Failed to update the data
                Log.e("FirebaseUpdate", "Error updating data: ${e.message}")
                // Handle the error
            }

        val appointmentsRef = database.getReference("appointments")

        val appointmentDetails = HashMap<String, Any>()
        appointmentDetails["monitorName"] = Monitorname.text.toString()
        appointmentDetails["startTime"] = slotName.text.toString()
        appointmentDetails["endTime"] = endTimeTextView.text.toString()
        appointmentDetails["passcode"] = passcode.toString()

        appointmentsRef
            .child(userId.toString())
            .push() // Use push to generate a unique key for each appointment
            .setValue(appointmentDetails)
            .addOnSuccessListener {
                // Successfully added the appointment details
                // You can perform any additional actions here
            }
            .addOnFailureListener { e ->
                // Failed to add the appointment details
                Log.e("FirebaseAddAppointment", "Error adding appointment: ${e.message}")
                // Handle the error
            }

    }

    override fun onPaymentError(p0: Int, p1: String?) {

        scrollLayout.visibility = View.GONE
        failureLayout.visibility = View.VISIBLE

    }
}