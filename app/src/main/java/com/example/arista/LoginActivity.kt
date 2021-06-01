package com.example.arista

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.arista.data.model.LoginUser
import com.example.arista.ui.main.viewmodel.MainViewModel
import com.example.arista.utils.Status
import kotlinx.android.synthetic.main.activity_login.*

class  LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Login"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener() {
            viewModel = MainViewModel()

            var loginUser = LoginUser()
            loginUser.email = editTextLoginName.text.toString().trim()
            loginUser.password = editTextLoginPassword.text.toString().trim()

            //startActivity(Intent(this, HomeActivity::class.java))
            viewModel.doLogin(loginUser, token).observe(this, Observer { networkResource ->
                when (networkResource.status) {
                    Status.LOADING -> {
                        Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        val map = networkResource.data
                        map?.let {
                            Toast.makeText(this, map["status"].toString(), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)

                            val editor = token.edit()
                            editor.putString("user_id", map["user_id"].toString())
                            editor.putString("isLoggedIn", loginUser.email)
                            editor.commit()

                            startActivity(intent)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}