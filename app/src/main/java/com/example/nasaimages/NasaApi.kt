package com.example.nasaimages

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for interacting with NASA's APOD (Astronomy Picture of the Day) API.
 */
interface NasaApi {
    /**
     * Retrieves the Astronomy Picture of the Day (APOD) using the provided API key.
     *
     * @param apiKey The API key for accessing the APOD API.
     * @return A [Call] object representing the API call. You can enqueue this call to make
     *         the request asynchronously.
     */
    @GET("apod")
    fun getImageOfTheDay(
        @Query("api_key") apiKey: String
    ): Call<NasaImageResponse>
}




