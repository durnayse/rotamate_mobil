package com.example.rotamate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rotamate.api.CountryApiResponse
import com.example.rotamate.api.CountryApiService
import com.example.rotamate.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesAdapter(
    private val fullList: MutableList<FavoriteCountry>,      // 🔹 TÜM favoriler
    private val filteredList: MutableList<FavoriteCountry>,  // 🔹 Arama sonrası liste
    private val onItemClick: (FavoriteCountry) -> Unit,
    private val onItemLongClick: (FavoriteCountry) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    // 🔹 API servisi (tek sefer)
    private val api: CountryApiService =
        RetrofitClient.retrofit.create(CountryApiService::class.java)

    // 🔹 Bayrak cache (performans)
    private val flagCache = mutableMapOf<String, String>()

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
        val imgFlag: ImageView = itemView.findViewById(R.id.imgFlag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_country, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val favorite = filteredList[position]
        val countryName = favorite.countryName

        holder.tvCountryName.text = countryName

        // 🔹 Placeholder (recycle bug önler)
        holder.imgFlag.setImageResource(R.drawable.ic_launcher_foreground)

        // 🔹 Cache varsa direkt yükle
        val cachedFlag = flagCache[countryName]
        if (cachedFlag != null) {
            Glide.with(holder.itemView.context)
                .load(cachedFlag)
                .into(holder.imgFlag)
        } else {
            // 🔹 API çağrısı (1 kere)
            api.getCountryByName(countryName)
                .enqueue(object : Callback<List<CountryApiResponse>> {
                    override fun onResponse(
                        call: Call<List<CountryApiResponse>>,
                        response: Response<List<CountryApiResponse>>
                    ) {
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            val flagUrl = response.body()!![0].flags.png
                            flagCache[countryName] = flagUrl

                            Glide.with(holder.itemView.context)
                                .load(flagUrl)
                                .into(holder.imgFlag)
                        }
                    }

                    override fun onFailure(call: Call<List<CountryApiResponse>>, t: Throwable) {
                        // Sessiz geç
                    }
                })
        }

        holder.itemView.setOnClickListener {
            onItemClick(favorite)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(favorite)
            true
        }
    }

    override fun getItemCount(): Int = filteredList.size

    /* ===============================
       🔍 FAVORİLERDE ARAMA FİLTRESİ
       =============================== */
    fun filter(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val lowerQuery = query.lowercase()
            filteredList.addAll(
                fullList.filter {
                    it.countryName.lowercase().contains(lowerQuery)
                }
            )
        }

        notifyDataSetChanged()
    }
}