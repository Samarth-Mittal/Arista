package com.example.arista

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.arista.data.model.SignUpUser
import com.example.arista.ui.main.viewmodel.MainViewModel
import com.example.arista.utils.Status
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        title = "Signup"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        btnSignUp.setOnClickListener() {
            viewModel = MainViewModel()

            var signupUser = SignUpUser()
            signupUser.fname = editTextSignupFName.text.toString().trim()
            signupUser.mname = editTextSignupMName.text.toString().trim()
            signupUser.lname = editTextSignupLName.text.toString().trim()
            signupUser.email = editTextSignupEmail.text.toString().trim()
            signupUser.mobile = editTextSignupMobile.text.toString().trim()
            signupUser.password = editTextSignupPassword.text.toString().trim()
            val cPassword = editTextSignupCPassword.text.toString().trim()

            if(cPassword.equals(signupUser.password)) {

                viewModel.doRegistration(signupUser, token)
                    .observe(this, Observer { networkResource ->
                        when (networkResource.status) {
                            Status.LOADING -> {
                                Toast.makeText(this, "Signing up", Toast.LENGTH_SHORT).show()
                            }
                            Status.SUCCESS -> {
                                val map = networkResource.data
                                map?.let {
                                    //Toast.makeText(this, map["status"].toString(), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, HomeActivity::class.java)

                                    val editor = token.edit()
                                    editor.putString("user_id", map["user_id"].toString())
                                    editor.putString("isLoggedIn", signupUser.email)
                                    editor.commit()

                                    startActivity(intent)
                                    finish()
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    })
            }else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }
}