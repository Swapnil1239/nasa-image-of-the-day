package com.example.nasaimages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ViewModel responsible for fetching NASA's Astronomy Picture of the Day (APOD) data
 * and handling associated LiveData.
 */
class MyViewModel: ViewModel() {
    // LiveData to observe response data.
    private val _data = MutableLiveData<NasaImageResponse>()
    val data: LiveData<NasaImageResponse> = _data

    // LiveData to observe error reponse.
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Base URL for the NASA API.
    private val baseUrl = "https://api.nasa.gov/planetary/"

    /**
     * Fetches the Astronomy Picture of the Day (APOD) data from the NASA API.
     */
    fun fetchImageOfTheDay() {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nasaApi = retrofit.create(NasaApi::class.java)

        val call = nasaApi.getImageOfTheDay(API_KEY)
        call.enqueue(object : Callback<NasaImageResponse> {
            override fun onResponse(call: Call<NasaImageResponse>, response: Response<NasaImageResponse>) {
                if (response.isSuccessful) {
                    _data.value = response.body()
                } else {
                    _error.value = "API Request Failed with Status Code: ${response.code()}"
                }
            }
            override fun onFailure(call: Call<NasaImageResponse>, t: Throwable) {
                _error.value = "Network or other error: ${t.message}"
            }
        })
    }

    companion object {
        private const val API_KEY = "eNzgKatN1DDzcQaSnP9a1a62HvMs4NhqZh5cghg5"
    }
}