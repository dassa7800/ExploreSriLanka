package com.example.exploresrilanka

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser ?: return view // Handle the case where user might be null

        // Get references to UI elements
        val userNameTextView: TextView = view.findViewById(R.id.user_name)
        val userEmailTextView: TextView = view.findViewById(R.id.user_email)
        val privacyPolicyButton: Button = view.findViewById(R.id.privacy_policy_button)
        val termsConditionsButton: Button = view.findViewById(R.id.terms_conditions_button)
        val logoutButton: Button = view.findViewById(R.id.logout_button)

        // Set user name and email
        userNameTextView.text = user.displayName ?: "No Name Provided"
        userEmailTextView.text = user.email ?: "No Email Provided"

        // Set click listeners for the buttons
        privacyPolicyButton.setOnClickListener {
            // Handle Privacy Policy click
            Toast.makeText(context, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
            // You can start a new activity or load a web page here
        }

        termsConditionsButton.setOnClickListener {
            // Handle Terms and Conditions click
            Toast.makeText(context, "Terms and Conditions Clicked", Toast.LENGTH_SHORT).show()
            // You can start a new activity or load a web page here
        }

        logoutButton.setOnClickListener {
            // Handle Logout
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            Toast.makeText(context, "Logout successful!", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        return view
    }
}