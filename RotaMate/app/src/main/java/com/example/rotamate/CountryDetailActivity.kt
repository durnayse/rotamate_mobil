package com.example.rotamate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rotamate.api.CountryApiResponse
import com.example.rotamate.api.CountryApiService
import com.example.rotamate.api.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountryDetailActivity : AppCompatActivity() {

    private lateinit var imgFavorite: ImageView
    private var isFavorite = false

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)

        // 🔹 View’lar
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvRegion = findViewById<TextView>(R.id.tvDetailRegion)
        val tvPopulation = findViewById<TextView>(R.id.tvDetailPopulation)
        val tvCapital = findViewById<TextView>(R.id.tvDetailCapital)
        val tvCurrency = findViewById<TextView>(R.id.tvDetailCurrency)
        val tvLanguages = findViewById<TextView>(R.id.tvDetailLanguages)
        val imgCountry = findViewById<ImageView>(R.id.imgDetailCountry)
        imgFavorite = findViewById(R.id.imgFavorite)
        val btnOpenMap = findViewById<Button>(R.id.btnOpenMap)

        // 🔹 Ülke adı
        val countryName = intent.getStringExtra("countryName")
        if (countryName.isNullOrEmpty()) {
            Toast.makeText(this, "Ülke adı alınamadı", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 Kullanıcı & favori referansı
        val user = auth.currentUser
        val favRef = user?.let {
            db.collection("users")
                .document(it.uid)
                .collection("favorites")
                .document(countryName)
        }

        /* ===============================
           ❤️ FAVORİ DURUMUNU KONTROL ET
           =============================== */
        if (user != null) {
            favRef?.get()?.addOnSuccessListener { doc ->
                isFavorite = doc.exists()
                updateFavoriteIcon()
            }
        } else {
            updateFavoriteIcon()
        }

        /* ===============================
           ❤️ FAVORİ TOGGLE
           =============================== */
        imgFavorite.setOnClickListener {
            if (user == null) {
                Toast.makeText(this, "Favori eklemek için giriş yapmalısın", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isFavorite) {
                favRef?.delete()
                isFavorite = false
                Toast.makeText(this, "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show()
            } else {
                favRef?.set(
                    mapOf(
                        "countryName" to countryName,
                        "addedAt" to System.currentTimeMillis()
                    )
                )
                isFavorite = true
                Toast.makeText(this, "Favorilere eklendi ❤️", Toast.LENGTH_SHORT).show()
            }

            updateFavoriteIcon()
        }

        /* ===============================
           🗺 HARİTADA GÖSTER
           =============================== */
        btnOpenMap.setOnClickListener {
            val query = Uri.encode(countryName)
            val geoUri = Uri.parse("geo:0,0?q=$query")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
                    )
                )
            }
        }

        /* ===============================
           🌍 REST COUNTRIES API
           =============================== */
        val api = RetrofitClient.retrofit.create(CountryApiService::class.java)

        api.getCountryByName(countryName)
            .enqueue(object : Callback<List<CountryApiResponse>> {

                override fun onResponse(
                    call: Call<List<CountryApiResponse>>,
                    response: Response<List<CountryApiResponse>>
                ) {
                    if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                        Toast.makeText(
                            this@CountryDetailActivity,
                            "Ülke bilgisi bulunamadı",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val country = response.body()!![0]

                    tvName.text = country.name.common
                    tvRegion.text = "Bölge: ${country.region ?: "Bilinmiyor"}"
                    tvPopulation.text = "Nüfus: ${country.population ?: "Bilinmiyor"}"
                    tvCapital.text =
                        "Başkent: ${country.capital?.joinToString(", ") ?: "Bilinmiyor"}"

                    val currency = country.currencies?.values?.firstOrNull()
                    tvCurrency.text =
                        "Para Birimi: ${currency?.name ?: "Bilinmiyor"} ${currency?.symbol ?: ""}"

                    tvLanguages.text =
                        "Diller: ${country.languages?.values?.joinToString(", ") ?: "Bilinmiyor"}"

                    Glide.with(this@CountryDetailActivity)
                        .load(country.flags.png)
                        .into(imgCountry)
                }

                override fun onFailure(call: Call<List<CountryApiResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@CountryDetailActivity,
                        "API Hatası: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /* ===============================
       ❤️ KALP İKONUNU GÜNCELLE
       =============================== */
    private fun updateFavoriteIcon() {
        if (isFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            imgFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}