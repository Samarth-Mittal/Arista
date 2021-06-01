package com.example.arista

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var screenOnOffReceiver: ScreenOnOffReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backgroundService = Intent(
            applicationContext,
            ScreenOnOffBackgroundService::class.java
        )
        startService(backgroundService)

        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Activity onCreate")

        title = "Arista"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = token.edit()

        if (!token.getString("isLoggedIn", "").equals("")) {
            btnLogin_Dashboard.text = "Open Dashboard"
            btnSignup_Logout.text = "Logout"
            btnLogin_Dashboard.setOnClickListener() {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            btnSignup_Logout.setOnClickListener() {
                editor.clear()
                editor.putString("isLoggedIn", "")
                editor.commit()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            btnLogin_Dashboard.text = "Login"
            btnSignup_Logout.text = "Signup"
            editor.putInt("isFirstLogin", 0)
            editor.commit()
            btnLogin_Dashboard.setOnClickListener() {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            btnSignup_Logout.setOnClickListener() {
                startActivity(Intent(this, SignupActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Activity onDestroy")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
            }
        }
    }
}