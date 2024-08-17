package com.example.exploresrilanka

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destination(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val distanceFromAirport: String = "",
    val nearbyHotels: String = "",
    val nearbyDestinations: String = "",
    val category: String = ""
) : Parcelable