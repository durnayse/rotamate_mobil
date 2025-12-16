package com.example.rotamate

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rotamate.databinding.ActivitySurveyBinding
import com.google.firebase.firestore.FirebaseFirestore

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private val db = FirebaseFirestore.getInstance()

    private val budgets = listOf("düşük", "orta", "yüksek")
    private val climates = listOf("sıcak", "ılıman", "soğuk")
    private val activities = listOf("deniz", "kültür", "doğa", "gece hayatı")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupSeekBar()

        binding.btnGetRecommendation.setOnClickListener {
            getRecommendation()
        }
    }

    private fun setupSpinners() {
        binding.spBudget.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, budgets)
        binding.spClimate.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, climates)
        binding.spActivity.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activities)
    }

    private fun setupSeekBar() {
        binding.seekFlightTime.progress = 8
        binding.tvFlightTimeValue.text = "8 saat"

        binding.seekFlightTime.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvFlightTimeValue.text = "$progress saat"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun getRecommendation() {

        val selectedBudget = budgets[binding.spBudget.selectedItemPosition]
        val selectedClimate = climates[binding.spClimate.selectedItemPosition]
        val selectedActivity = activities[binding.spActivity.selectedItemPosition]
        val maxFlight = binding.seekFlightTime.progress

        db.collection("countries").get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    Toast.makeText(this, "Henüz ülke kaydı yok.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val countries = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Country::class.java)
                }

                val scored = countries.map { c ->
                    var score = 0
                    if (c.budgetLevel == selectedBudget) score += 30
                    if (c.climate == selectedClimate) score += 25
                    if (c.activityType == selectedActivity) score += 25
                    if (c.flightTime <= maxFlight) score += 20
                    Pair(c, score)
                }

                val sorted = scored.sortedByDescending { it.second }

                if (sorted.isEmpty()) {
                    Toast.makeText(this, "Uygun ülke bulunamadı.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val bestCountry = sorted[0].first
                val bestScore = sorted[0].second
                val top3 = sorted.drop(1).take(3)

                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("countryName", bestCountry.name)
                intent.putExtra("score", bestScore)
                intent.putExtra("imageUrl", bestCountry.imageUrl)
                intent.putExtra("description", bestCountry.description)
                intent.putExtra("apiName", bestCountry.apiName)

                if (top3.size > 0) {
                    intent.putExtra("top1Name", top3[0].first.name)
                    intent.putExtra("top1Image", top3[0].first.imageUrl)
                }
                if (top3.size > 1) {
                    intent.putExtra("top2Name", top3[1].first.name)
                    intent.putExtra("top2Image", top3[1].first.imageUrl)
                }
                if (top3.size > 2) {
                    intent.putExtra("top3Name", top3[2].first.name)
                    intent.putExtra("top3Image", top3[2].first.imageUrl)
                }

                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Ülke verisi alınamadı: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}