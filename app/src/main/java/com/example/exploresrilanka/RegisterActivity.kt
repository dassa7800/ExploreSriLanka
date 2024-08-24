package com.example.exploresrilanka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exploresrilanka.databinding.ActivityRegisterBinding
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confPassword = binding.confirmPasswordInput.text.toString().trim()

            if (name.isEmpty()) {
                binding.nameInput.error = "Name is required"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailInput.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInput.error = "Password is required"
                return@setOnClickListener
            }

            if (password != confPassword) {
                binding.confirmPasswordInput.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Determine the user role based on the email
            val role = if (email == "admin@gmail.com") "admin" else "user"

            // Register the user with Firebase
            SplashActivity.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update the user's profile with their name
                        val user = SplashActivity.auth.currentUser
                        user?.let {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                            it.updateProfile(profileUpdates)
                                .addOnCompleteListener { profileTask ->
                                    if (profileTask.isSuccessful) {
                                        // Store user role in Firestore
                                        storeUserRole(user.uid, email, role)

                                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to update profile: ${profileTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Function to store user role in Firestore
    private fun storeUserRole(uid: String, email: String, role: String) {
        val userMap = hashMapOf(
            "uid" to uid,
            "email" to email,
            "role" to role
        )

        firestore.collection("users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User role saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user role: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}