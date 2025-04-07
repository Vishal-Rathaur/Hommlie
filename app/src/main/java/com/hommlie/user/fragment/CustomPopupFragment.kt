package com.hommlie.user.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.hommlie.customfonts.FixedSizeVideoView
import com.hommlie.user.R

class CustomPopupFragment : DialogFragment() {

    private lateinit var mediaType: String
    private lateinit var mediaToShow: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the arguments
        arguments?.let {
            mediaType = it.getString("key_media_type", "")
            mediaToShow = it.getString("key_media_to_show", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your popup layout
        return inflater.inflate(R.layout.fragment_custom_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views
        val imageView = view.findViewById<ImageView>(R.id.iv_image)
        val videoView = view.findViewById<FixedSizeVideoView>(R.id.video)
        val closeButton = view.findViewById<ImageView>(R.id.iv_close)
        val loadingIndicator = view.findViewById<ProgressBar>(R.id.loading_indicator)

        // Show/Hide views based on media type
        when (mediaType) {
            "Video" -> {
                videoView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                loadingIndicator.visibility = View.VISIBLE

                // Set up the video view and add listeners
                videoView.setVideoPath(mediaToShow)
                videoView.setOnPreparedListener { mediaPlayer ->
                    // Video is prepared, hide loading indicator
                    loadingIndicator.visibility = View.GONE
                    mediaPlayer.start() // Start playing the video
                }
                videoView.setOnErrorListener { mp, what, extra ->
                    // Handle errors if needed
                    loadingIndicator.visibility = View.GONE
                    false // Return false to indicate that we haven't handled the error
                }
            }
            "image" -> {
                imageView.visibility = View.VISIBLE
                videoView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                Glide.with(this)
                    .load(mediaToShow)
                    .placeholder(R.drawable.placeholder_homevideo)
                    .into(imageView)
            }
            else -> {
                // Handle other cases or invalid media types
            }
        }

        // Handle close button click
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        // Set the dialog's width and height
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        // Method to create an instance of the fragment with arguments
        fun newInstance(mediaType: String, mediaToShow: String): CustomPopupFragment {
            val fragment = CustomPopupFragment()
            val args = Bundle()
            args.putString("key_media_type", mediaType)
            args.putString("key_media_to_show", mediaToShow)
            fragment.arguments = args
            return fragment
        }
    }
}
