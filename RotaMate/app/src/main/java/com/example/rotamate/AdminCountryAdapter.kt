package com.example.rotamate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdminCountryAdapter(
    private val countryList: List<Country>
) : RecyclerView.Adapter<AdminCountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCountryName)
        val imgCountry: ImageView = itemView.findViewById(R.id.imgCountry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countryList[position]

        holder.tvName.text = country.name

        Glide.with(holder.itemView.context)
            .load(country.imageUrl)
            .into(holder.imgCountry)
    }

    override fun getItemCount(): Int = countryList.size
}