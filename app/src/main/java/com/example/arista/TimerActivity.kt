package com.example.arista

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.arista.data.model.SendHelp
import com.example.arista.ui.main.viewmodel.MainViewModel
import com.example.arista.utils.Constants.Companion.USER_LOCATION
import com.example.arista.utils.Status
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    lateinit var timePickerFragment: TimePickerFragment
    var userID: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        userID = getUserId(token.getString("user_id", "0.0"))

        if(intent.hasExtra("Timer")){
            onTimerEnd()
            revertUI()
        }

        btnSetTimer.setOnClickListener(){
            timePickerFragment = TimePickerFragment()
            timePickerFragment.show(supportFragmentManager, "Countdown Timer")
        }
        btnSendHelpTimer.setOnClickListener(){
            onTimerEnd()
            timePickerFragment.countDownTimer.cancel()
            revertUI()
        }
        btnStopTimer.setOnClickListener(){
            timePickerFragment.countDownTimer.cancel()
            revertUI()
        }
    }

    private fun getUserId(userId: String?): Long {
        return userId?.substring(0,userId.indexOf("."))!!.toLong()
    }

    fun onTimerEnd(){
        val viewModel = MainViewModel()
        val userId = userID
        val sendHelp = SendHelp(userId, "I might be in trouble", "https://www.google.com/maps/search/?api=1&query=" + USER_LOCATION)
        viewModel.sendHelp(sendHelp).observe(this, Observer { networkResource ->
            when (networkResource.status) {
                Status.LOADING -> {
                    Toast.makeText(this, "Sending SOS", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    val message = networkResource.data
                    message?.let {
                        Toast.makeText(this, "SOS Sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(this, "SOS not sent", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun revertUI() {
        textViewTimer.text = "By what time do you expect to reach your destination?"
        linearLayoutTimer.visibility = View.GONE
        btnSetTimer.visibility = View.VISIBLE
    }
}