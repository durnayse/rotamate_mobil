package com.example.rotamate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rotamate.databinding.ActivityUserHomeBinding
import com.google.firebase.auth.FirebaseAuth

class UserHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHomeBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🧭 Tatil önerisi sorularına git
        binding.btnStartSurvey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }

        // ❤️ Favoriler ekranına git
        binding.btnFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // 🚪 Çıkış yap
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}