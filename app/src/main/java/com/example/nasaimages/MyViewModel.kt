package com.example.nasaimages

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
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

    // SharedPreferences file name
    private val PREFS_NAME = "NasaImagesPrefs"

    // Function to save data to SharedPreferences
    private fun saveData(context: Context, key: String, data: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key, data)
        editor.apply()
    }

    // Function to retrieve data from SharedPreferences
    private fun retrieveData(context: Context, key: String): String? {
        android.util.Log.d("zzzz","retrieveData")
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, null)
    }

    /**
     * Fetches the Astronomy Picture of the Day (APOD) data from the NASA API.
     */
    fun fetchImageOfTheDay(context: Context) {
        // Check if cached data exists in SharedPreferences
        val cachedData = retrieveData(context, "cached_apod_data")
        if (cachedData != null) {
            val cachedResponse = Gson().fromJson(cachedData, NasaImageResponse::class.java)
            _data.value = cachedResponse
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val nasaApi = retrofit.create(NasaApi::class.java)

        val call = nasaApi.getImageOfTheDay(API_KEY)
        call.enqueue(object : Callback<NasaImageResponse> {
            override fun onResponse(call: Call<NasaImageResponse>, response: Response<NasaImageResponse>) {
                if (response.isSuccessful) {
                    // Get the newly fetched response data
                    val newResponse = response.body()

                    // Check if the new data is different from the cached data
                    if (newResponse != null && !newResponse.equals(cachedData)) {
                        // Update LiveData with the new response data
                        _data.value = newResponse

                        // Save the new response data and timestamp to SharedPreferences for caching
                        val responseDataJson = Gson().toJson(newResponse)
                        saveData(context, "cached_apod_data", responseDataJson)
                        saveData(context, "cached_apod_timestamp", System.currentTimeMillis().toString())
                    }
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