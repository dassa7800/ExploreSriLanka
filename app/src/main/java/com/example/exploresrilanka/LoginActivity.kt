package com.example.exploresrilanka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exploresrilanka.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                SplashActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show()
                            fetchUserRoleAndRedirect(SplashActivity.auth.currentUser?.uid)
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun fetchUserRoleAndRedirect(uid: String?) {
        if (uid == null) {
            Toast.makeText(this, "Error: User ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        // Redirect to AdminActivity if the role is admin
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        // Redirect to HomeActivity for regular users
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user role: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}