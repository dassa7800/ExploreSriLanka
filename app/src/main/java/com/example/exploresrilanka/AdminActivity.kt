package com.example.exploresrilanka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.admin_bottom_navigation)

        // Set up bottom navigation listener
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(AdminHomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_explore -> {
                    loadFragment(AdminExploreFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_bookings -> {
                    loadFragment(AdminBookingsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        // Load the default fragment
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun loadFragment(fragment: Fragment) {
        // Load fragment into the frame layout container
        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container, fragment)
            .commit()
    }
}