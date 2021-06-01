package com.example.arista

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*
import java.util.concurrent.TimeUnit

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {

    lateinit var countDownTimer: CountDownTimer
    var userID: Long = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val token = activity?.getSharedPreferences("User", Context.MODE_PRIVATE)
        userID = getUserId(token?.getString("user_id", "0.0"))

        return TimePickerDialog(context, this, hour, minute, true)
    }

    private fun getUserId(userId: String?): Long {
        return userId?.substring(0,userId.indexOf("."))!!.toLong()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val currentTime = Calendar.getInstance()
        val selectedTime = Calendar.getInstance()
        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedTime.set(Calendar.MINUTE, minute)
        if(selectedTime.timeInMillis < currentTime.timeInMillis){
            Toast.makeText(activity, "Selected time has already passed.", Toast.LENGTH_LONG).show()
        }else{
            val timerTime = selectedTime.timeInMillis - currentTime.timeInMillis
            val textViewTimer = activity?.findViewById<TextView>(R.id.textViewTimer)
            val contextValue = context
            countDownTimer = object : CountDownTimer(timerTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val timerTime: String = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1))
                    textViewTimer?.text = "Time Remaining: " + timerTime
                }

                override fun onFinish() {
                    val intent = Intent(contextValue, TimerActivity::class.java)
                    intent.putExtra("Timer", "1")
                    contextValue?.startActivity(intent)
                    cancel()
                }
            }
            val btnSetTimer = activity?.findViewById<Button>(R.id.btnSetTimer)
            val linearLayoutTimer = activity?.findViewById<LinearLayout>(R.id.linearLayoutTimer)
            linearLayoutTimer?.visibility = View.VISIBLE
            btnSetTimer?.visibility = View.GONE
            countDownTimer.start()
        }
    }


}