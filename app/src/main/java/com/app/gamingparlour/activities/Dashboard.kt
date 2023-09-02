package com.app.gamingparlour.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.gamingparlour.R
import com.app.gamingparlour.fragments.homeFragment
import com.app.gamingparlour.fragments.orderFragment
import com.app.gamingparlour.fragments.profileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {

    var bottom_navigation: BottomNavigationView? = null
    val HomeFragment = homeFragment()
    val ProfileFragment = profileFragment()
    val OrderFragement = orderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        window.statusBarColor = resources.getColor(R.color.black);

        bottom_navigation = findViewById(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment).commit()
        bottom_navigation!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment)
                        .commit()

                    return@OnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ProfileFragment).commit()


                    return@OnNavigationItemSelectedListener true
                }
                R.id.myorder -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, OrderFragement).commit()

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onBackPressed() {
        val currentFragment =
            this.supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is profileFragment || currentFragment is orderFragment) {
            supportFragmentManager.beginTransaction()
                .apply { replace(R.id.container, HomeFragment) .commit() }
        } else {
            super.onBackPressed()
        }
    }


}