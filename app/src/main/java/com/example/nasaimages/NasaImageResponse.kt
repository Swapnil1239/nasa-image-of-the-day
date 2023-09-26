package com.example.nasaimages

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response from NASA's Astronomy Picture of the Day (APOD) API.
 *
 * @property date The date associated with the image.
 * @property explanation A detailed explanation or caption for the image.
 * @property mediaType The type of media content, e.g., "image" or "video".
 * @property serviceVersion The version of the NASA API service.
 * @property title The title or name of the image.
 * @property url The URL of the image or video content.
 */
data class NasaImageResponse(
    @SerializedName("date") val date: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("service_version") val serviceVersion: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)