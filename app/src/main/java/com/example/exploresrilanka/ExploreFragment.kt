package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExploreFragment : Fragment() {

    private lateinit var destinationAdapter: DestinationAdapter
    private lateinit var destinationsList: MutableList<Destination>
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.destinations_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        destinationsList = mutableListOf()
        destinationAdapter = DestinationAdapter(destinationsList, false)
        recyclerView.adapter = destinationAdapter

        // Fetch data from Firestore
        fetchDestinations()

        return view
    }

    private fun fetchDestinations() {
        firestore.collection("destinations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val destination = document.toObject(Destination::class.java)
                    destinationsList.add(destination)
                }
                destinationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                exception.printStackTrace()
            }
    }
}