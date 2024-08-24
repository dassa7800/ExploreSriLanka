package com.example.exploresrilanka

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class AdminExploreFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var destinationRecyclerView: RecyclerView
    private lateinit var destinationAdapter: DestinationAdapter
    private val destinationList = mutableListOf<Destination>()
    private val categories = arrayOf("General", "Wellness", "Adventure")
    private val selectedCategories = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_explore, container, false)

        firestore = FirebaseFirestore.getInstance()
        destinationRecyclerView = view.findViewById(R.id.destination_recycler_view)
        destinationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        destinationAdapter = DestinationAdapter(destinationList, true) // Pass true for isAdmin
        destinationRecyclerView.adapter = destinationAdapter

        val addDestinationFab: FloatingActionButton = view.findViewById(R.id.add_destination_fab)
        addDestinationFab.setOnClickListener {
            showAddDestinationDialog()
        }

        loadDestinationsFromFirebase()

        return view
    }

    private fun loadDestinationsFromFirebase() {
        firestore.collection("destinations")
            .get()
            .addOnSuccessListener { result ->
                destinationList.clear()
                for (document in result) {
                    val destination = document.toObject(Destination::class.java)
                    destinationList.add(destination)
                }
                destinationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting destinations: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddDestinationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_destination, null)
        builder.setView(dialogView)

        val nameInput = dialogView.findViewById<EditText>(R.id.destination_name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.destination_description_input)
        val imageInput = dialogView.findViewById<EditText>(R.id.destination_image_input)
        val distanceFromAirportInput = dialogView.findViewById<EditText>(R.id.distance_from_airport_input)
        val nearbyHotelsInput = dialogView.findViewById<EditText>(R.id.nearby_hotels_input)
        val nearbyDestinationsInput = dialogView.findViewById<EditText>(R.id.nearby_destinations_input)
        val selectCategoryButton = dialogView.findViewById<Button>(R.id.select_category_button)
        val selectedCategoriesText = dialogView.findViewById<TextView>(R.id.selected_categories_text)

        selectCategoryButton.setOnClickListener {
            showCategorySelectionDialog(selectedCategoriesText)
        }

        builder.setTitle("Add New Destination")
        builder.setPositiveButton("Add") { dialog, _ ->
            val name = nameInput.text.toString()
            val description = descriptionInput.text.toString()
            val image = imageInput.text.toString()
            val distanceFromAirport = distanceFromAirportInput.text.toString()
            val nearbyHotels = nearbyHotelsInput.text.toString()
            val nearbyDestinations = nearbyDestinationsInput.text.toString()
            val category = selectedCategories.joinToString(", ")

            if (name.isNotEmpty() && description.isNotEmpty() && image.isNotEmpty() && category.isNotEmpty() && distanceFromAirport.isNotEmpty() && nearbyHotels.isNotEmpty() && nearbyDestinations.isNotEmpty()) {
                val newDestination = Destination(
                    id = firestore.collection("destinations").document().id,
                    name = name,
                    description = description,
                    image = image,
                    distanceFromAirport = distanceFromAirport,
                    nearbyHotels = nearbyHotels,
                    nearbyDestinations = nearbyDestinations,
                    category = category
                )

                firestore.collection("destinations").document(newDestination.id)
                    .set(newDestination)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Destination added successfully", Toast.LENGTH_SHORT).show()
                        destinationList.add(newDestination)
                        destinationAdapter.notifyItemInserted(destinationList.size - 1)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to add destination: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showCategorySelectionDialog(selectedCategoriesText: TextView) {
        val checkedItems = BooleanArray(categories.size)
        categories.forEachIndexed { index, category ->
            checkedItems[index] = selectedCategories.contains(category)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Categories")
            .setMultiChoiceItems(categories, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    if (!selectedCategories.contains(categories[which])) {
                        selectedCategories.add(categories[which])
                    }
                } else {
                    selectedCategories.remove(categories[which])
                }
            }
            .setPositiveButton("OK") { dialog, _ ->
                updateSelectedCategoriesText(selectedCategoriesText)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateSelectedCategoriesText(selectedCategoriesText: TextView) {
        if (selectedCategories.isEmpty()) {
            selectedCategoriesText.text = "Selected Categories: None"
        } else {
            selectedCategoriesText.text = "Selected Categories: ${selectedCategories.joinToString(", ")}"
        }
    }
}