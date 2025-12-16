package com.example.rotamate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rotamate.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->

                val uid = authResult.user?.uid ?: return@addOnSuccessListener

                // 🔥 ADIM 2 — UID GÖSTER (EN KRİTİK KONTROL)
                Toast.makeText(this, "AUTH UID: $uid", Toast.LENGTH_LONG).show()
                Log.d("LOGIN_UID", uid)

                val userRef = db.collection("users").document(uid)

                userRef.get()
                    .addOnSuccessListener { doc ->

                        // 🔹 KULLANICI YOKSA → OLUŞTUR (VERİ SİLMEZ)
                        if (!doc.exists()) {
                            val newUser = hashMapOf(
                                "email" to email,
                                "role" to "user",
                                "status" to "active"
                            )

                            userRef.set(newUser, SetOptions.merge())
                                .addOnSuccessListener {
                                    goUserHome()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Kullanıcı oluşturulamadı",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        } else {
                            // 🔹 KULLANICI VAR
                            val status = doc.getString("status") ?: "active"
                            val role = doc.getString("role") ?: "user"

                            Toast.makeText(
                                this,
                                "ROLE: $role | STATUS: $status",
                                Toast.LENGTH_LONG
                            ).show()

                            if (status == "passive") {
                                Toast.makeText(
                                    this,
                                    "Hesabınız pasif durumda.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@addOnSuccessListener
                            }

                            if (role == "admin") {
                                startActivity(Intent(this, AdminHomeActivity::class.java))
                                finish()
                            } else {
                                goUserHome()
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Giriş başarısız: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun goUserHome() {
        startActivity(Intent(this, UserHomeActivity::class.java))
        finish()
    }
}