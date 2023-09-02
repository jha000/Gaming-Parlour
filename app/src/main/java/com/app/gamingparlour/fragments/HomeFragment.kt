package com.app.gamingparlour.fragments

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gamingparlour.R
import com.app.gamingparlour.activities.MainActivity
import com.app.gamingparlour.adapter.MonitorAdapter
import com.app.gamingparlour.adapter.SlotAdapter
import com.app.gamingparlour.data.Monitor
import com.app.gamingparlour.data.Slot
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.util.*

class homeFragment : Fragment(), MonitorAdapter.MonitorSelectionListener {

    var linearLayoutManager: LinearLayoutManager? = null
    private var selectedMonitorName: String = "Monitor 1"
    private lateinit var monitorname: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var address: TextView

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.monitorsRecyclerView)
        val slotsRecyclerView: RecyclerView = view.findViewById(R.id.slots)
        monitorname = view.findViewById(R.id.monitorname)

        val sharedPreferences =
            requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)

        sharedPreferences.edit().apply {
            putString("Monitorname", monitorname.text.toString())
            apply()
        }

        val pic: ImageView = view.findViewById(R.id.pic)
//
//        pic.setOnClickListener{
//            val intent = Intent(activity, profileFragment::class.java)
//            startActivity(intent)
//        }

        address = view.findViewById(R.id.address)

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE

        val slotsLayoutManager = LinearLayoutManager(requireContext())
        slotsRecyclerView.layoutManager = slotsLayoutManager

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()


        linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val monitorList = mutableListOf<Monitor>()

        val monitorsReference = FirebaseDatabase.getInstance().getReference("monitors")

        monitorsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val monitorList = mutableListOf<Monitor>()
                for (monitorSnapshot in snapshot.children) {
                    val id = monitorSnapshot.key
                    val name = monitorSnapshot.child("name").getValue(String::class.java)
                    val totalSlots = monitorSnapshot.child("slots").childrenCount.toInt()

                    // Add debug logs to check the retrieved data
                    Log.d("MonitorDebug", "ID: $id, Name: $name, Total Slots: $totalSlots")

                    if (id != null && name != null) {
                        monitorList.add(Monitor(id, name, totalSlots))
                    }
                }

                // Initialize and set up the adapter with the populated monitorList
                val monitorAdapter = MonitorAdapter(monitorList, this@homeFragment)
                recyclerView.adapter = monitorAdapter

                loadingProgressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MonitorDebug", "Database Error: ${error.message}")
                // Handle read error
            }
        })

        val database = FirebaseDatabase.getInstance()
        val monitorsRef = database.getReference("monitors")

        val selectedMonitorRef = monitorsRef.child(selectedMonitorName)

        selectedMonitorRef.child("slots")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (isAdded) {
                        val slotList = mutableListOf<Slot>()

                        for (slotSnapshot in dataSnapshot.children) {
                            val slot = slotSnapshot.getValue(Slot::class.java)
                            slot?.let {
                                slotList.add(it)
                            }
                        }

                        for (slot in slotList) {
                            Log.d(
                                "SlotDebug",
                                "Slot: startTime=${slot.startTime}, endTime=${slot.endTime}, status=${slot.status}"
                            )
                        }


                        val slotrv: RecyclerView = view.findViewById(R.id.slots)
                        val adapter = SlotAdapter(requireContext(), slotList)
                        slotrv.adapter = adapter

                        loadingProgressBar.visibility = View.GONE
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SlotDebug", "Database Error: ${databaseError.message}")
                    // Handle read error
                }
            })


        return view
    }

    override fun onMonitorSelected(name: String) {
        selectedMonitorName = name
        monitorname.text = selectedMonitorName

        val slotrv: RecyclerView = requireView().findViewById(R.id.slots)
        loadingProgressBar.visibility = View.VISIBLE
        slotrv.visibility=View.GONE

        val sharedPreferences =
            requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)

        sharedPreferences.edit().apply {
            putString("Monitorname", name)
            apply()
        }

        val database = FirebaseDatabase.getInstance()
        val monitorsRef = database.getReference("monitors")
        val selectedMonitorRef = monitorsRef.child(selectedMonitorName)

        selectedMonitorRef.child("slots")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (isAdded) {
                        val slotList = mutableListOf<Slot>()

                        for (slotSnapshot in dataSnapshot.children) {
                            val slot = slotSnapshot.getValue(Slot::class.java)
                            slot?.let {
                                slotList.add(it)
                            }
                        }

                        for (slot in slotList) {
                            Log.d(
                                "SlotDebug",
                                "Slot: startTime=${slot.startTime}, endTime=${slot.endTime}, status=${slot.status}"
                            )
                        }

                        val slotrv: RecyclerView = view!!.findViewById(R.id.slots)
                        val adapter = SlotAdapter(requireContext(), slotList)
                        slotrv.adapter = adapter

                        loadingProgressBar.visibility = View.GONE
                        slotrv.visibility=View.VISIBLE
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SlotDebug", "Database Error: ${databaseError.message}")
                    // Handle read error
                }
            })
        Log.d("HomeFragment", "Selected Monitor: $name")
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses: List<Address>? =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            address.text = addresses!![0].locality + ", " + addresses[0].adminArea

                            val sharedPref = requireActivity().getSharedPreferences(
                                "myKey",
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPref.edit()
                            editor.putString("address", address.text.toString())
                            editor.apply()

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
        } else {
            askPermission()
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }


    override fun onResume() {
        super.onResume()
        getLastLocation()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()

            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please provide the required permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
