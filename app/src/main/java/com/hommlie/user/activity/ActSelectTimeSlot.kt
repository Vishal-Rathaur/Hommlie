package com.hommlie.user.activity

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hommlie.user.adapter.DateAdapter
import com.hommlie.user.adapter.TimeAdapter
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActSelectTimeSlotBinding
import com.hommlie.user.model.DateTimeModel
import com.hommlie.user.utils.Common
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class ActSelectTimeSlot : BaseActivity() {

    private lateinit var binding: ActivityActSelectTimeSlotBinding

    var date:String=""
    var time:String=""
    private var dateTimeList = mutableListOf<DateTimeModel>()

    companion object {
        var selectedDate: String = ""
        var selectedTime: String = ""
    }

    override fun setLayout(): View =binding.root

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding=ActivityActSelectTimeSlotBinding.inflate(layoutInflater)

        selectedDate=""
        selectedTime=""


        val today = LocalDate.now()


        for (i in 0..10) {
            val fullFutureDate =today.plusDays((i.toLong())+1)
            val futureDate = today.plusDays((i.toLong())+1)
            val dayOfMonth = futureDate.dayOfMonth.toString()
            val dayOfWeek = futureDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3)
            val time=1+i
            val finaltime= "0$time:00 PM"
            dateTimeList.add(DateTimeModel(fullFutureDate.toString(),dayOfMonth, dayOfWeek, finaltime))

        }
        val adapter = DateAdapter(this,dateTimeList)
        binding.rvDate.adapter = adapter
        binding.rvDate.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val timeadapter = TimeAdapter(this,generateTimeSlots(9,19))
        binding.rvTime.adapter = timeadapter
        binding.rvTime.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)



        binding.btnGetDateTime.setOnClickListener{
            if (selectedDate!=""){
                if (selectedTime!=""){
                    val isComeFromSelectAddress =
                        intent.getBooleanExtra("isComeFromSelectDateandTimeMethod", false)
                    if (isComeFromSelectAddress) {
                        val intent = Intent(this@ActSelectTimeSlot, ActCheckout::class.java)
                        intent.putExtra("date", selectedDate)
                        intent.putExtra("time", selectedTime)
                        setResult(502, intent)
                        finish()
                    }
                }else{
                    Common.showErrorFullMsg(this@ActSelectTimeSlot,"Please select Time")
                }
            }else{
                Common.showErrorFullMsg(this@ActSelectTimeSlot,"Please select Date")
            }
        }

    }


    fun generateTimeSlots(startHour: Int, endHour: Int): List<String> {
        val timeSlots = mutableListOf<String>()

//        for (hour in startHour until endHour) {
//            val startTime = formatTime(hour)
//            val endTime = formatTime(hour + 2)
//            timeSlots.add("$startTime")//- $endTime")
//        }
        var currentHour = startHour
        while (currentHour < endHour) {
            val startTime = formatTime(currentHour)
            timeSlots.add(startTime)
            currentHour += 2
        }
        return timeSlots
    }

    fun formatTime(hour: Int): String {
        val period = if (hour >= 12) "PM" else "AM"
        val formattedHour = when {
            hour == 0 -> 12 // Convert 0 hour to 12 AM
            hour > 12 -> hour - 12 // Convert to 12-hour format
            else -> hour
        }
        return String.format("%02d:00 %s", formattedHour, period)
    }


}