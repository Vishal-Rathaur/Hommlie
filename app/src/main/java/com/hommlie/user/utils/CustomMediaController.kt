package com.hommlie.user.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.MediaController
import com.hommlie.user.R

class CustomMediaController(context: Context, attrs: AttributeSet? = null) : MediaController(context, attrs) {

    private var playPauseButton: ImageButton? = null

    init {
        // Inflate the custom layout
//        LayoutInflater.from(context).inflate(R.layout.custom_media_controller, this, true)
//        playPauseButton = findViewById(R.id.playPauseButton)
//        setupPlayPauseButton()
    }

    private fun setupPlayPauseButton() {
        playPauseButton?.setOnClickListener {
            mediaPlayerControl?.let { control ->
                if (control.isPlaying) {
                    control.pause()
                    playPauseButton?.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    control.start()
                    playPauseButton?.setImageResource(android.R.drawable.ic_media_pause)
                }
            }
        }
    }

    // Override the setMediaPlayer method from MediaController
    override fun setMediaPlayer(player: MediaPlayerControl?) {
        super.setMediaPlayer(player)
        mediaPlayerControl = player
        // Update play/pause button based on player state
        playPauseButton?.let {
            if (player?.isPlaying == true) {
                it.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                it.setImageResource(android.R.drawable.ic_media_play)
            }
        }
    }

    override fun setAnchorView(view: View?) {
        super.setAnchorView(view)
        // Ensure that view is not null
        view?.post {
            // Perform any additional setup if needed
        }
    }

    private var mediaPlayerControl: MediaPlayerControl? = null
}


