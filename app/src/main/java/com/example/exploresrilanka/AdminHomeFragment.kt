package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        val viewHotelListButton: Button = view.findViewById(R.id.view_hotel_list_button)
        val viewDestinationsListButton: Button = view.findViewById(R.id.view_destinations_list_button)

        viewHotelListButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, AdminBookingsFragment())
                .addToBackStack(null)
                .commit()

            (activity as? AdminActivity)?.findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)?.selectedItemId = R.id.nav_bookings
        }

        viewDestinationsListButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, AdminExploreFragment())
                .addToBackStack(null)
                .commit()

            (activity as? AdminActivity)?.findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)?.selectedItemId = R.id.nav_explore
        }

        return view
    }
}