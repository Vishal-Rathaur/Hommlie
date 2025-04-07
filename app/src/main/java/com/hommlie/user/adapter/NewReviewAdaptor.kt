package com.hommlie.user.adapter

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.model.Rattings
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewReviewAdaptor(
    private val context: Context,
    private val reviews: List<Rattings>
) : RecyclerView.Adapter<NewReviewAdaptor.ReviewViewHolder>() {

    // ViewHolder class to hold item views
    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRate : LinearLayout = itemView.findViewById(R.id.iv_rate)
        val ivUserImage: ImageView = itemView.findViewById(R.id.iv_userImage)
        val tvUserName: TextView = itemView.findViewById(R.id.tv_userName)
        val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_newreview, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
     //   holder.tvUserName.text = review.name
     //   Glide.with(holder.itemView.context).load(review.image).placeholder(R.drawable.placeholder).into(holder.ivUserImage)
        holder.tvMessage.text = review.comment
        if (review.createdAt!=null){
            holder.tvDate.text =formatRelativeDate(review.createdAt)
        }else{
            holder.tvDate.visibility=View.GONE
        }

        val averageRating = 4.6f
        displayDynamicStars(holder.ivRate,averageRating,holder)
    }

    override fun getItemCount(): Int = reviews.size


    private fun displayDynamicStars(
        rating1: LinearLayout,
        rating: Float,
        holder: ReviewViewHolder
    ) {
        rating1.removeAllViews()

        for (i in 0 until 5) {
            val starView = ImageView(holder.itemView.context)

            // Combine empty and filled stars into a LayerDrawable
            val emptyDrawable =holder.itemView.context.getDrawable(R.drawable.ic_star_empty)
            val filledDrawable = ClipDrawable(holder.itemView.context.getDrawable(R.drawable.ic_filled_star), Gravity.LEFT, ClipDrawable.HORIZONTAL)

            // Set the level (fill percentage) for each star
            val starLevel = calculateStarFillLevel(rating - i)
            filledDrawable.level = starLevel

            // Apply LayerDrawable combining empty and filled stars
            val layerDrawable = LayerDrawable(arrayOf(emptyDrawable, filledDrawable))
            starView.setImageDrawable(layerDrawable)

            // Set star size
            val params = LinearLayout.LayoutParams(45, 45) // Width x Height
            params.setMargins(4, 0, 4, 0)
            starView.layoutParams = params

            // Add the starView to the LinearLayout
            rating1.addView(starView)
        }
    }

    /**
     * Calculates the fill level for a star:
     * - 0 -> empty
     * - 10000 -> full
     * - Any intermediate value for fractional fill
     */
    private fun calculateStarFillLevel(value: Float): Int {
        return when {
            value >= 1 -> 10000  // Fully filled star
            value > 0 -> (value * 10000).toInt()  // Partially filled star
            else -> 0  // Empty star
        }
    }



    fun formatRelativeDate(dateString: String): String {
        try {
            // Define the input date format
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Handle UTC time

            // Parse the input date
            val inputDate = dateFormat.parse(dateString) ?: return "Invalid date"

            // Get current date and time
            val now = Calendar.getInstance()
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            return when {
                // Check if the date is today
                inputDate >= today.time -> "Today"

                // Check if the date is yesterday
                inputDate >= yesterday.time -> "Yesterday"

                // Calculate days difference for older dates
                else -> {
                    val diffInMillis = today.timeInMillis - inputDate.time
                    val daysAgo = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
                    "$daysAgo days ago"
                }
            }
        } catch (e: Exception) {
            return "Invalid date"
        }
    }
}