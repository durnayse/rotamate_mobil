package com.example.rotamate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminCountryAdapter
    private val countryList = mutableListOf<Country>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        recyclerView = findViewById(R.id.recyclerCountries)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminCountryAdapter(countryList)
        recyclerView.adapter = adapter

        loadCountriesFromFirestore()
    }

    private fun loadCountriesFromFirestore() {
        db.collection("countries")
            .get()
            .addOnSuccessListener { snapshot ->
                countryList.clear()

                for (doc in snapshot.documents) {
                    val country = doc.toObject(Country::class.java)
                    if (country != null) {
                        countryList.add(country)
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Ülkeler yüklenemedi",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}