package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class ItinerariesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var itinerariesRecyclerView: RecyclerView
    private lateinit var itinerariesAdapter: ItinerariesAdapter
    private val itinerariesList = mutableListOf<Itinerary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_itineraries, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Setup RecyclerView
        itinerariesRecyclerView = view.findViewById(R.id.itineraries_recycler_view)
        itinerariesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itinerariesAdapter = ItinerariesAdapter(itinerariesList) { selectedItinerary ->
            showItineraryDetails(selectedItinerary) // Handle item click
        }
        itinerariesRecyclerView.adapter = itinerariesAdapter

        // Load itineraries
        loadItineraries()

        return view
    }

    private fun loadItineraries() {
        val userEmail = auth.currentUser?.email ?: return

        firestore.collection("itineraries")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                itinerariesList.clear() // Clear previous data
                for (document in documents) {
                    val itinerary = document.toObject(Itinerary::class.java).apply {
                        documentId = document.id // Store document ID
                    }
                    itinerariesList.add(itinerary)
                }
                itinerariesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting itineraries: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showItineraryDetails(itinerary: Itinerary) {
        // Create a new fragment instance for itinerary details
        val itineraryDetailsFragment = ItineraryDetailsFragment()

        // Pass the selected itinerary data to the new fragment using Serializable
        val bundle = Bundle().apply {
            putSerializable("itinerary", itinerary) // Use putSerializable to pass the Itinerary object
        }
        itineraryDetailsFragment.arguments = bundle

        // Navigate to ItineraryDetailsFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, itineraryDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}

// Data Class to Represent an Itinerary
data class Itinerary(
    val userEmail: String = "",
    val userName: String = "",
    val itinerary: String = "",
    val createdAt: Long = 0L,
    var documentId: String = "" // Add this field to store the Firebase document ID
) : Serializable