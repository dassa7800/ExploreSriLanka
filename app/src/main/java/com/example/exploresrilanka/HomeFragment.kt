package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var welcomeTextView: TextView
    private lateinit var generateItineraryButton: Button
    private lateinit var generateAnotherItineraryButton: Button
    private lateinit var viewItineraryButton: Button
    private lateinit var regenerateItineraryButton: Button

    private lateinit var openAIApiService: OpenAIApiService
    private var generatedItinerary: String? = null
    private var lastPrompt: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = auth.currentUser ?: return view

        welcomeTextView = view.findViewById(R.id.home_welcome)
        generateItineraryButton = view.findViewById(R.id.generate_itinerary_button)
        generateAnotherItineraryButton = view.findViewById(R.id.generate_another_itinerary_button)
        regenerateItineraryButton = view.findViewById(R.id.regenerate_itinerary_button)
        viewItineraryButton = view.findViewById(R.id.view_itinerary_button)

        val username = user.displayName ?: "User"
        welcomeTextView.text = "Welcome $username!"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openAIApiService = retrofit.create(OpenAIApiService::class.java)

        checkForSavedItinerary()

        generateItineraryButton.setOnClickListener {
            showItineraryDialog()
        }

        generateAnotherItineraryButton.setOnClickListener {
            showItineraryDialog()
        }

        regenerateItineraryButton.setOnClickListener {
            lastPrompt?.let { prompt ->
                generateItinerary(prompt)
            }
        }

        viewItineraryButton.setOnClickListener {
            showItinerariesFragment()
        }

        view.findViewById<Button>(R.id.save_itinerary_button).setOnClickListener {
            saveItinerary()
        }

        return view
    }

    private fun checkForSavedItinerary() {
        val userEmail = user.email ?: return

        firestore.collection("itineraries")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Show only the initial generate itinerary button
                    generateItineraryButton.visibility = View.VISIBLE
                    viewItineraryButton.visibility = View.GONE
                    generateAnotherItineraryButton.visibility = View.GONE
                } else {
                    // Show view itinerary and generate another itinerary buttons
                    generateItineraryButton.visibility = View.GONE
                    viewItineraryButton.visibility = View.VISIBLE
                    generateAnotherItineraryButton.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to check itineraries: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showItinerariesFragment() {
        val itinerariesFragment = ItinerariesFragment()
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, itinerariesFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = R.id.navigation_itineraries
    }

    private fun showItineraryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_itinerary_options, null)

        val daysInput = dialogView.findViewById<EditText>(R.id.days_input)
        val preferenceSpinner = dialogView.findViewById<Spinner>(R.id.preference_spinner)
        val budgetInput = dialogView.findViewById<EditText>(R.id.budget_input)
        val peopleInput = dialogView.findViewById<EditText>(R.id.people_input)
        val roomsInput = dialogView.findViewById<EditText>(R.id.rooms_input)

        val preferences = arrayOf("General", "Adventure", "Wellness")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, preferences)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        preferenceSpinner.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Itinerary Options")
            .setView(dialogView)
            .setPositiveButton("Next") { dialog, _ ->
                val days = daysInput.text.toString()
                val preference = preferenceSpinner.selectedItem.toString()
                val budget = budgetInput.text.toString()
                val people = peopleInput.text.toString()
                val rooms = roomsInput.text.toString()

                val prompt = buildItineraryPrompt(days, preference, budget, people, rooms)
                lastPrompt = prompt
                generateItinerary(prompt)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun buildItineraryPrompt(days: String, preference: String, budget: String, people: String, rooms: String): String {
        return "Generate a ${preference.toLowerCase()} travel itinerary for a ${days}-day trip to Sri Lanka with a budget of ${budget} USD for ${people} people requiring ${rooms} rooms."
    }

    private fun generateItinerary(prompt: String) {
        val messages = listOf(
            Message(role = "user", content = prompt)
        )
        val request = OpenAIRequest(messages = messages)

        openAIApiService.generateItinerary(request).enqueue(object : Callback<OpenAIResponse> {
            override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                if (response.isSuccessful) {
                    generatedItinerary = response.body()?.choices?.get(0)?.message?.content
                    generatedItinerary?.let {
                        showGeneratedItinerary(it)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context, "Error: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                    errorBody?.let { println("Error Body: $it") }
                }
            }

            override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showGeneratedItinerary(itinerary: String) {
        val itineraryLayout = view?.findViewById<LinearLayout>(R.id.itinerary_scroll_view)
        val itineraryTextView = view?.findViewById<TextView>(R.id.generated_itinerary_text)
        val buttonsLayout = view?.findViewById<LinearLayout>(R.id.itinerary_buttons_layout)

        itineraryTextView?.text = itinerary

        itineraryLayout?.visibility = View.VISIBLE
        buttonsLayout?.visibility = View.VISIBLE
    }

    private fun saveItinerary() {
        val userId = user.uid
        val userEmail = user.email ?: "user@email.com"
        val userName = user.displayName ?: "User"
        val itinerary = generatedItinerary ?: return

        // Get the current date and time
        val createdAt = System.currentTimeMillis()

        val itineraryData = mapOf(
            "userId" to userId,
            "userEmail" to userEmail,
            "userName" to userName,
            "itinerary" to itinerary,
            "createdAt" to createdAt // Save created date and time
        )

        firestore.collection("itineraries")
            .add(itineraryData)
            .addOnSuccessListener {
                Toast.makeText(context, "Itinerary saved.", Toast.LENGTH_SHORT).show()
                // Hide generate itinerary button and show view itinerary button
                generateItineraryButton.visibility = View.GONE
                viewItineraryButton.visibility = View.VISIBLE
                generateAnotherItineraryButton.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save itinerary: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}