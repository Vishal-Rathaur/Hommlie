package com.hommlie.user.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors.getColor
import com.hommlie.user.R
import com.hommlie.user.activity.ActProductDetails
import com.hommlie.user.databinding.ActProductDetailsBinding
import com.hommlie.user.databinding.RowBhkBinding
import com.hommlie.user.databinding.RowProductsizeBinding
import com.hommlie.user.model.DynamicVariationData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.CustomBulletSpan
import kotlin.random.Random

class VariationAdaptor(
    private val context: Context,
    private val variations: List<DynamicVariationData>,
    private val productDetailsBinding: ActProductDetailsBinding,
    private val onVariationSelected: (String) -> Unit
) : RecyclerView.Adapter<VariationAdaptor.VariationViewHolder>() {

    private var selectedPosition: Int = -1

    init {

        if (variations.isNotEmpty()) {
            selectedPosition = 0
        }
    }

    inner class VariationViewHolder(private val binding: RowBhkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val data = variations[position]

            binding.tvproductsizeS.text = data.variation
            binding.tvPrice.text ="\u20b9 "+ data.discountedVariationPrice
            binding.tvOrginalPrice.text ="\u20b9 "+ data.price
            binding.tvRating.text = getRandomNumber(4.4,5.0).toString()+" ("+getRandomNumber(700.0,1500.0).toInt()+" reviews)"

            if (position == selectedPosition) {
                binding.llBhk.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_primary))
             //   binding.llBhk.background = ResourcesCompat.getDrawable(context.resources, R.drawable., null)
             //   binding.tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.llBhk.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.LightGray_Color))
            //    binding.tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.color_545454))
            }

            binding.root.setOnClickListener {
                if (selectedPosition != position) {
                    variations.forEach { it.isSelected = false } // Deselect all items
                    variations[position].isSelected = true // Select clicked item
                    selectedPosition = position
                    notifyDataSetChanged()

                    // Trigger the callback with the selected variation name
                    onVariationSelected(data.variation ?: "")

                    if (data.description.isNullOrEmpty()) {
                        productDetailsBinding.selectedServiceDesc.visibility = View.GONE
                    } else {
                        productDetailsBinding.selectedServiceDesc.visibility = View.VISIBLE

                        val points = variations[selectedPosition].description?.split("|")

                        val spannableString = SpannableStringBuilder()

                        points?.forEachIndexed { index, point ->
                            if (point.isNotEmpty()) { // Ensure it's not empty
                                val start = spannableString.length
                                spannableString.append(point.trim())

                                spannableString.setSpan(
                                    CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                                    start,
                                    spannableString.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                // Add extra line height spacing
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    spannableString.setSpan(
                                        LineHeightSpan.Standard(55), // Adjust for more/less space
                                        start,
                                        spannableString.length,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }

                                // Append a newline only if it's not the last item to avoid extra bullets
                                if (index != points.size - 1) {
                                    spannableString.append("\n")
                                }
                            }
                        }
                        productDetailsBinding.selectedServiceDesc.text = spannableString
                    }



                    if (data.variationTimes.toString().isNullOrEmpty()){
                        productDetailsBinding.noOfService.visibility=View.GONE
                    }else{
                        productDetailsBinding.noOfService.visibility=View.VISIBLE
                      //  productDetailsBinding.noOfService.text="\u2022 No. of services : "+data.variationTimes+" Times"
                        val spannableString = SpannableStringBuilder()
                        val text = "No. of services: ${data.variationTimes} Times"
                        val start = spannableString.length

                        spannableString.append(text)

// Apply CustomBulletSpan for a bullet point
                        spannableString.setSpan(
                            CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                            start,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        productDetailsBinding.noOfService.text = spannableString
                    }


                    if (data.variationInterval.isNullOrEmpty()){
                        productDetailsBinding.secheduleEvery.visibility=View.GONE
                    }else{
                        productDetailsBinding.secheduleEvery.visibility=View.VISIBLE
                       // productDetailsBinding.secheduleEvery.text="\u2022 Scheduled every : "+data.variationInterval+" Days"
                        val spannableString = SpannableStringBuilder()

                        val text = "Scheduled every: ${data.variationInterval} Days"
                        val start = spannableString.length

                        spannableString.append(text)

// Apply CustomBulletSpan for a bullet point
                        spannableString.setSpan(
                            CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                            start,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

// Set the formatted text to TextView
                        productDetailsBinding.secheduleEvery.text = spannableString

                    }
                    productDetailsBinding.tvCurrentPrice.text="\u20b9"+variations[selectedPosition].discountedVariationPrice
                    productDetailsBinding.tvOldPrice.text="\u20b9"+variations[selectedPosition].price
                    productDetailsBinding.tvdiscountpercentage.text= variations[selectedPosition].price?.let {
                        variations[selectedPosition].discountedVariationPrice?.let { it1 ->
                            calculateDiscountPercentage(it.toDouble(), it1.toDouble()).toString()+"% off"
                        }
                    }
                    productDetailsBinding.tvTime.visibility=View.GONE

                    val priceString = variations[selectedPosition].price
                    val discountedPriceString = variations[selectedPosition].discountedVariationPrice
                    if (priceString!=null) {
                        ActProductDetails.selectedPackagePrice = priceString.toDoubleOrNull() ?: 0.0
                    }else{
                        ActProductDetails.selectedPackagePrice = 0.0
                    }
                    if (discountedPriceString!=null) {
                        ActProductDetails.selectedDisPackagePrice = discountedPriceString.toDoubleOrNull() ?: 0.0
                    }else{
                        ActProductDetails.selectedDisPackagePrice = 0.0
                    }

                    ActProductDetails.selectedVariationId = variations[position].id ?:0
                    ActProductDetails.selectedAttributeId = variations[position].attributeId ?:0


                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariationViewHolder {
        val binding = RowBhkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VariationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VariationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = variations.size


    // Method to select the first item
    fun selectFirstItem() {
        if (variations.isNotEmpty()) {
            variations.forEach { it.isSelected = false } // Deselect all items
            variations[0].isSelected = true // Select the first item
            notifyDataSetChanged() // Notify the adapter to refresh the view
            // Trigger the callback with the first item's variation

            onVariationSelected(variations[0].variation ?: "")

//            if (variations[selectedPosition].description.isNullOrEmpty()){
//                productDetailsBinding.selectedServiceDesc.visibility=View.GONE
//            }else{
//                productDetailsBinding.selectedServiceDesc.visibility=View.VISIBLE
//                productDetailsBinding.selectedServiceDesc.text=variations[selectedPosition].description
//            }


            if (variations[selectedPosition].description.isNullOrEmpty()) {
                productDetailsBinding.selectedServiceDesc.visibility = View.GONE
            } else {
                productDetailsBinding.selectedServiceDesc.visibility = View.VISIBLE

                // Split the description by "|"
                val points = variations[selectedPosition].description?.split("|")

                val spannableString = SpannableStringBuilder()

                points?.forEachIndexed { index, point ->
                    if (point.isNotEmpty()) { // Ensure it's not empty
                        val start = spannableString.length
                        spannableString.append(point.trim())

                        spannableString.setSpan(
                            CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                            start,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Add extra line height spacing
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            spannableString.setSpan(
                                LineHeightSpan.Standard(55), // Adjust for more/less space
                                start,
                                spannableString.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

                        // Append a newline only if it's not the last item to avoid extra bullets
                        if (index != points.size - 1) {
                            spannableString.append("\n")
                        }
                    }
                }

                productDetailsBinding.selectedServiceDesc.text = spannableString
            }




            if (variations[selectedPosition].variationTimes.toString().isNullOrEmpty()){
                productDetailsBinding.noOfService.visibility=View.GONE
            }else{
                productDetailsBinding.noOfService.visibility=View.VISIBLE
             //   productDetailsBinding.noOfService.text="No. of services : "+variations[selectedPosition].variationTimes+" Times"

                val spannableString = SpannableStringBuilder()

                val text = "No. of services: ${variations[selectedPosition].variationTimes} Times"
                val start = spannableString.length

                spannableString.append(text)

// Apply CustomBulletSpan for a bullet point
                spannableString.setSpan(
                    CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                    start,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

// Set the formatted text to TextView
                productDetailsBinding.noOfService.text = spannableString

            }


            if (variations[selectedPosition].variationInterval.isNullOrEmpty()){
                productDetailsBinding.secheduleEvery.visibility=View.GONE
            }else{
                productDetailsBinding.secheduleEvery.visibility=View.VISIBLE
            //    productDetailsBinding.secheduleEvery.text="\u2022 Scheduled every : "+variations[selectedPosition].variationInterval+" Days"

                val spannableString = SpannableStringBuilder()

                val text = "Scheduled every: ${variations[selectedPosition].variationInterval} Days"
                val start = spannableString.length

                spannableString.append(text)

                spannableString.setSpan(
                    CustomBulletSpan(20, ContextCompat.getColor(context, R.color.home_header)),
                    start,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                productDetailsBinding.secheduleEvery.text = spannableString
            }


            productDetailsBinding.tvTime.visibility=View.GONE

            val priceString = variations[selectedPosition].price
            val discountedPriceString = variations[selectedPosition].discountedVariationPrice

            productDetailsBinding.tvCurrentPrice.text="\u20b9"+discountedPriceString
            productDetailsBinding.tvOldPrice.text="\u20b9"+priceString
            val calculatedDiscount = calculateDiscountPercentage(discountedPriceString?.toDouble()
                ?: 0.0, priceString?.toDouble() ?: 0.0)

            if (calculatedDiscount>0&&calculatedDiscount!=null){
                productDetailsBinding.tvdiscountpercentage.text= calculatedDiscount.toString()+"% off"
            }else{
                productDetailsBinding.tvdiscountpercentage.visibility=View.GONE
                productDetailsBinding.ivDownarrow.visibility=View.GONE
            }


            if (priceString!=null) {
                ActProductDetails.selectedPackagePrice = priceString.toDoubleOrNull() ?: 0.0
            }else{
                ActProductDetails.selectedPackagePrice = 0.0
            }
            if (discountedPriceString!=null) {
                ActProductDetails.selectedDisPackagePrice = discountedPriceString.toDoubleOrNull() ?: 0.0
            }else{
                ActProductDetails.selectedDisPackagePrice = 0.0
            }
            ActProductDetails.selectedVariationId = variations[selectedPosition].id ?:0
            ActProductDetails.selectedAttributeId = variations[selectedPosition].attributeId ?:0

        }
    }


    fun calculateDiscountPercentage(originalPrice: Double, discountedPrice: Double): Int {
        if (originalPrice <= 0 || discountedPrice < 0) {
         //   throw IllegalArgumentException("Prices must be positive and original price should be greater than 0")
             Common.getLog("price",""+discountedPrice)
        }
        val discount = originalPrice - discountedPrice
        val discountPercentage = (discount / originalPrice) * 100
        return discountPercentage.toInt() // Round to 2 decimal places
    }


    fun getRandomNumber(start: Double, end: Double): Double {
        return String.format("%.1f", Random.nextDouble(start, end)).toDouble()
    }

}
