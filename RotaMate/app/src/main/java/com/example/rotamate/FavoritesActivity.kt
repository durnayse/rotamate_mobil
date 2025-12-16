package com.example.rotamate

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private lateinit var tvEmpty: TextView
    private lateinit var searchView: SearchView

    // 🔹 TÜM FAVORİLER + FİLTRELİ LİSTE
    private val fullFavorites = mutableListOf<FavoriteCountry>()
    private val filteredFavorites = mutableListOf<FavoriteCountry>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.recyclerFavorites)
        tvEmpty = findViewById(R.id.tvEmptyFavorites)
        searchView = findViewById(R.id.searchFavorites)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FavoritesAdapter(
            fullFavorites,
            filteredFavorites,
            onItemClick = { favorite ->
                val intent = Intent(this, CountryDetailActivity::class.java)
                intent.putExtra("countryName", favorite.countryName)
                startActivity(intent)
            },
            onItemLongClick = { favorite ->
                removeFavorite(favorite)
            }
        )

        recyclerView.adapter = adapter

        setupSearch()
        setupSwipeToDelete()
        loadFavoritesFromFirestore()
    }

    /* ===============================
       🔍 ARAMA
       =============================== */
    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                updateEmptyState()
                return true
            }
        })
    }

    /* ===============================
       👉 SWIPE + KIRMIZI ARKA PLAN
       =============================== */
    private fun setupSwipeToDelete() {

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favorite = filteredFavorites[position]
                removeFavorite(favorite)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint().apply {
                    color = ContextCompat.getColor(this@FavoritesActivity, R.color.swipe_red)
                }

                val icon: Drawable? =
                    ContextCompat.getDrawable(this@FavoritesActivity, R.drawable.ic_delete_white)
                if (dX < 0) { // sola kaydır
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    icon?.let {
                        val margin = (itemView.height - it.intrinsicHeight) / 2
                        val top = itemView.top + margin
                        val right = itemView.right - margin
                        val left = right - it.intrinsicWidth
                        val bottom = top + it.intrinsicHeight
                        it.setBounds(left, top, right, bottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    /* ===============================
       🔥 FIRESTORE
       =============================== */
    private fun loadFavoritesFromFirestore() {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("favorites")
            .orderBy("addedAt")
            .get()
            .addOnSuccessListener { snapshot ->
                fullFavorites.clear()
                filteredFavorites.clear()

                for (doc in snapshot.documents) {
                    doc.toObject(FavoriteCountry::class.java)?.let {
                        fullFavorites.add(it)
                    }
                }

                filteredFavorites.addAll(fullFavorites)
                adapter.notifyDataSetChanged()
                updateEmptyState()
            }
    }

    /* ===============================
       ❤️ SİL
       =============================== */
    private fun removeFavorite(favorite: FavoriteCountry) {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("favorites")
            .document(favorite.countryName)
            .delete()
            .addOnSuccessListener {
                fullFavorites.remove(favorite)
                filteredFavorites.remove(favorite)
                adapter.notifyDataSetChanged()
                updateEmptyState()
            }
    }

    /* ===============================
       📭 BOŞ EKRAN
       =============================== */
    private fun updateEmptyState() {
        if (filteredFavorites.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}