package com.example.nasaimages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

/**
 * The main activity of the NASA Images app responsible for displaying
 * Astronomy Picture of the Day (APOD) content and handling user interactions.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var videoPlaceHolderImage: ImageView

    private lateinit var viewModel: MyViewModel

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: StyledPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        imageView = findViewById(R.id.imageView)
        videoPlaceHolderImage = findViewById(R.id.video_placeholder_image)
        titleTextView = findViewById(R.id.titleTextView)
        dateTextView = findViewById(R.id.dateTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        refreshButton = findViewById(R.id.refreshButton)
        playerView = findViewById(R.id.player_view)
        exoPlayer = ExoPlayer.Builder(this@MainActivity).build()
        playerView.player = exoPlayer

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        // Observe fetched response to update the UI.
        viewModel.data.observe(this) { responseData ->
            updateUiWithData(responseData)
            updateImageVisibility(responseData.mediaType)
        }

        // Observe response error to update the UI.
        viewModel.error.observe(this) { errorMessage ->
            showError(errorMessage)
        }

        // Set up a click listener for the refresh button.
        refreshButton.setOnClickListener {
            viewModel.fetchImageOfTheDay()
        }

        // Fetch the initial image of the day.
        viewModel.fetchImageOfTheDay()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.playWhenReady = false
        exoPlayer.release()
    }

    // Updates the imageView, videoPlaceholderImage based on the mediaType.
    private fun updateImageVisibility(mediaType: String) {
        if (mediaType == "image") {
            imageView.visibility = View.VISIBLE
            videoPlaceHolderImage.visibility = View.GONE
        } else {
            imageView.visibility = View.GONE
            videoPlaceHolderImage.visibility = View.VISIBLE
        }
    }

    // Updates UI when reponse data is updated.
    private fun updateUiWithData(responseData: NasaImageResponse) {
        imageView.visibility = View.VISIBLE
        titleTextView.text = responseData.title
        dateTextView.text = responseData.date
        descriptionTextView.text = responseData.explanation
        if (responseData.mediaType == "video") {
            startPlayback(responseData.url)
        } else {
            Glide.with(this@MainActivity)
                .load(responseData.url)
                .into(imageView)
        }
    }

    // Shows error in UI if there is error while fetching api response.
    private fun showError(errorMessage: String) {
        titleTextView.text = "Error"
        dateTextView.text = ""
        descriptionTextView.text = errorMessage
        imageView.setImageResource(R.drawable.placeholder_img)
    }

    // Starts the video playback.
    private fun startPlayback(videoPath: String) {
        playerView.visibility = View.VISIBLE
        val mediaItem = MediaItem.fromUri(videoPath)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }
}