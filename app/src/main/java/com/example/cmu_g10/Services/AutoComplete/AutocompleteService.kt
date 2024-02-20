package com.example.cmu_g10.Services.AutoComplete

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for the autocomplete API.
 */
interface GeoapifyApiService {
    @GET("v1/geocode/autocomplete")
    fun autocomplete(
        @Query("text") input: String,
        @Query("apiKey") apiKey: String,
        @Query("filter") filter: String = "countrycode:pt"
    ): Call<AutocompleteResponse>
}