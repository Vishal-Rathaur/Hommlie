package com.hommlie.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.hommlie.user.R
import com.hommlie.user.model.MediaType
import com.hommlie.user.model.MySlideModel

class MySlideAdapter(private val slideList: List<MySlideModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (slideList[position].type) {
            MediaType.IMAGE -> TYPE_IMAGE
            MediaType.VIDEO -> TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))
            TYPE_VIDEO -> VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(slideList[position].url)
            is VideoViewHolder -> holder.bind(slideList[position].url)
        }
    }

    override fun getItemCount(): Int = slideList.size

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is VideoViewHolder) {
            holder.pausePlayer() // Pause video playback when the view is detached
        }
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is VideoViewHolder) {
            holder.resumePlayer() // Resume video playback when the view is attached
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(url: String) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerView: PlayerView = itemView.findViewById(R.id.playerView)
        private val loadingSpinner: ProgressBar = itemView.findViewById(R.id.loadingSpinner)
        private val player: SimpleExoPlayer = SimpleExoPlayer.Builder(itemView.context).build()

        init {
            playerView.player = player
            player.repeatMode = ExoPlayer.REPEAT_MODE_ALL  // Loop the video

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            loadingSpinner.visibility = View.VISIBLE
                        }
                        Player.STATE_READY -> {
                            loadingSpinner.visibility = View.GONE
                        }
                        Player.STATE_ENDED -> {
                            loadingSpinner.visibility = View.GONE
                        }
                    }
                }
            })
        }

        fun bind(url: String) {
       //     playerView.player = player
            player.setMediaItem(com.google.android.exoplayer2.MediaItem.fromUri(url))
            player.prepare()
            player.playWhenReady = true

        }

        fun releasePlayer() {
            player.stop()
            player.release()
        }

        fun pausePlayer() {
            player.playWhenReady = false
        }

        fun resumePlayer() {
            player.playWhenReady = true
        }
    }

}