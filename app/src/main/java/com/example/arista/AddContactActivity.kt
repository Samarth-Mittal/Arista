package com.example.arista

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.arista.data.model.AddContact
import com.example.arista.data.model.SignUpUser
import com.example.arista.ui.main.viewmodel.MainViewModel
import com.example.arista.utils.Status
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnLogin
import kotlinx.android.synthetic.main.activity_signup.*

class AddContactActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        title = "Add Contacts"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        btnAddContact.setOnClickListener() {
            viewModel = MainViewModel()

            val user_id = token.getString("user_id", "0.0")?.let { it1 -> getUserID(it1) }

            var addContact = AddContact()
            addContact.user_id = user_id!!
            addContact.name = editTextContactName.text.toString().trim()
            addContact.number = editTextContactNumber.text.toString().trim()

            viewModel.addContact(addContact)
                .observe(this, Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                            Toast.makeText(this, "Adding Contact", Toast.LENGTH_SHORT).show()
                        }
                        Status.SUCCESS -> {
                            val map = networkResource.data
                            map?.let {
                                Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)

                                startActivity(intent)
                                finish()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(this, "Contact not added", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }
    }

    private fun getUserID(string: String): Int? {

        return  string.substring(0, string.indexOf(".")).toInt()

    }
}