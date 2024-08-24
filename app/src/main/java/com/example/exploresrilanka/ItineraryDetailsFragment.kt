package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ItineraryDetailsFragment : Fragment() {

    private lateinit var itineraryTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var itineraryIdTextView: TextView
    private lateinit var backArrow: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_itinerary_details, container, false)

        itineraryTextView = view.findViewById(R.id.itinerary_text)
        userNameTextView = view.findViewById(R.id.user_name_text)
        itineraryIdTextView = view.findViewById(R.id.itinerary_id_text)
        backArrow = view.findViewById(R.id.back_arrow)

        // Retrieve itinerary data from arguments
        val itinerary = arguments?.getSerializable("itinerary") as? Itinerary

        // Populate the views with itinerary data
        itinerary?.let {
            itineraryTextView.text = it.itinerary
            userNameTextView.text = "Itinerary for: ${it.userName}"
            itineraryIdTextView.text = "Itinerary ${it.documentId}" // Display Itinerary ID
        }

        // Handle back arrow click to navigate back
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}