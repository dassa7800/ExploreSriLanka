package com.example.exploresrilanka

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DestinationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_detail)

        // Get the destination object from the intent
        val destination = intent.getParcelableExtra<Destination>("destination")

        // Get references to UI elements
        val destinationImage: ImageView = findViewById(R.id.destination_image)
        val destinationName: TextView = findViewById(R.id.destination_name)
        val destinationDescription: TextView = findViewById(R.id.destination_description)
        val destinationDistance: TextView = findViewById(R.id.destination_distance)
        val nearbyHotels: TextView = findViewById(R.id.nearby_hotels)
        val nearbyDestinations: TextView = findViewById(R.id.nearby_destinations)
        val destinationCategory: TextView = findViewById(R.id.destination_category)

        // Set the data to UI elements
        destination?.let {
            destinationName.text = it.name
            destinationDescription.text = it.description
            destinationDistance.text = "Distance from Airport: ${it.distanceFromAirport}"
            nearbyHotels.text = "Nearby Hotels: ${it.nearbyHotels}"
            nearbyDestinations.text = "Nearby Destinations: ${it.nearbyDestinations}"
            destinationCategory.text = "Category: ${it.category}"

            // Load the image using Glide
            Glide.with(this)
                .load(it.image)
                .into(destinationImage)
        }
    }
}