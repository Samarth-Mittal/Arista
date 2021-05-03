package com.example.arista

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Arista"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = token.edit()

        if(!token.getString("isLoggedIn", "").equals("")){
            btnLogin_Dashboard.text = "Open Dashboard"
            btnSignup_Logout.text = "Logout"
            btnLogin_Dashboard.setOnClickListener(){
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
        }else {
            btnLogin_Dashboard.text = "Login"
            btnSignup_Logout.text = "Signup"
            editor.putInt("isFirstLogin", 0)
            editor.commit()
            btnLogin_Dashboard.setOnClickListener() {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            btnSignup_Logout.setOnClickListener() {
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }
        }
    }
}