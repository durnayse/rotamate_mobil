package com.example.rotamate.api

import com.google.gson.annotations.SerializedName

/**
 * REST Countries API response modeli
 * https://restcountries.com/v3.1/
 */
data class CountryApiResponse(

    @SerializedName("name")
    val name: CountryName,

    @SerializedName("flags")
    val flags: CountryFlags,

    @SerializedName("region")
    val region: String? = null,

    @SerializedName("population")
    val population: Long? = null,

    // 🔹 Başkent (liste olarak gelir)
    @SerializedName("capital")
    val capital: List<String>? = null,

    // 🔹 Para birimi (map olarak gelir)
    @SerializedName("currencies")
    val currencies: Map<String, Currency>? = null,

    @SerializedName("languages")
val languages: Map<String, String>? = null

)

/* -------- ALT MODELLER -------- */

data class CountryName(
    @SerializedName("common")
    val common: String
)

data class CountryFlags(
    @SerializedName("png")
    val png: String
)

data class Currency(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("symbol")
    val symbol: String? = null
)
