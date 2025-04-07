package com.hommlie.user.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.model.HomeAddVideo
import com.hommlie.user.utils.Common.filterHttps
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class SliderAdapterThoghtfulVedio(private val items: List<HomeAddVideo>) : RecyclerView.Adapter<SliderAdapterThoghtfulVedio.SliderViewHolder>() {

    inner class SliderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_thoughtful, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position : Int) {
        val imageView: ImageView = holder.itemView.findViewById(R.id.ivBanner)
        val playbtn: ImageView = holder.itemView.findViewById(R.id.play_btn)
        val youTubePlayerView: YouTubePlayerView = holder.itemView.findViewById(R.id.youtube_player_view)

        val videoUrl = items[position].video_url.toString()
        val videoId = extractVideoIdFromUrl(videoUrl)

        // Log to check the extracted Video ID for each item
        Log.d("YouTubeDebug", "Video ID for position $position: $videoId")

        // Load thumbnail using Glide
        Glide.with(imageView.context)
            .load(filterHttps(items[position].thumbnail))
            .placeholder(R.drawable.placeholder_homevideo)
            .into(imageView)

        // Initial state: Show play button & thumbnail, hide YouTube player
        playbtn.visibility = View.VISIBLE
        imageView.visibility = View.VISIBLE
        youTubePlayerView.visibility = View.INVISIBLE

        // Add YouTube Player Listener only ONCE
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.d("YouTubeDebug", "YouTube Player is Ready for position $position")

                // Set Play button click listener
                playbtn.setOnClickListener {
                    Log.d("YouTubeDebug", "Play button clicked for position $position") // Debugging Click

                    videoId?.let { id ->
                        imageView.visibility = View.GONE
                        playbtn.visibility = View.GONE
                        youTubePlayerView.visibility = View.VISIBLE

                        Log.d("YouTubeDebug", "Loading video for position $position: $id")
                        youTubePlayer.loadVideo(id, 0f) // Load and play video
                    } ?: run {
                        Log.e("YouTubeDebug", "Invalid Video ID for position $position")
                    }
                }
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        Log.d("YouTubeDebug", "Video is Playing for position $position")
                        imageView.visibility = View.GONE
                        playbtn.visibility = View.GONE
                        youTubePlayerView.visibility = View.VISIBLE
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        Log.d("YouTubeDebug", "Video Ended for position $position")
                        imageView.visibility = View.VISIBLE
                        playbtn.visibility = View.VISIBLE
                        youTubePlayerView.visibility = View.INVISIBLE
                    }
                    PlayerConstants.PlayerState.PAUSED -> {
                        Log.d("YouTubeDebug", "Video Paused for position $position")
                        imageView.visibility = View.VISIBLE
                        playbtn.visibility = View.VISIBLE
                        youTubePlayerView.visibility = View.INVISIBLE
                    }
                    else -> Log.d("YouTubeDebug", "Player State: $state for position $position")
                }
            }
        })
    }









    override fun getItemCount() = items.size

    private fun extractVideoIdFromUrl(url: String): String? {
        val regex = Regex("(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

    }
}