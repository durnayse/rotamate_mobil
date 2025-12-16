package com.example.rotamate.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApiService {

    // Örnek çağrı:
    // https://restcountries.com/v3.1/name/Turkey
    @GET("v3.1/name/{country}")
    fun getCountryByName(
        @Path("country") country: String
    ): Call<List<CountryApiResponse>>
}
