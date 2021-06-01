package com.example.arista.ui.main.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.arista.data.api.ApiClient
import com.example.arista.data.api.UserApi
import com.example.arista.data.model.*
import com.example.arista.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel : ViewModel() {

    val apiService = ApiClient.createService(UserApi::class.java)

    fun doLogin(loginUser: LoginUser, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val login_response = apiService.doLogin(loginUser)
        if (login_response.isSuccessful) {
            val editor = token.edit()
            editor.putString("UserID", login_response.body()?.get("user_id").toString())
            editor.commit()
            emit(Resource.success(login_response.body()))
        } else {
            emit(Resource.error(login_response.body().toString()))
        }
    }

    fun doRegistration(signUpUser: SignUpUser, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val signup_response = apiService.doRegister(signUpUser)
        if (signup_response.isSuccessful) {
            val editor = token.edit()
            editor.putString("UserID", signup_response.body()?.get("user_id").toString())
            editor.commit()
            emit(Resource.success(signup_response.body()))
        } else {
            emit(Resource.error(signup_response.body().toString()))
        }
    }

    fun addContact(addContact: AddContact) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val user = apiService.addContact(addContact)
        if (user.isSuccessful) {
            emit(Resource.success(user.body()?.toString()))
        } else {
            emit(Resource.error(user.body()?.get("status").toString()))
        }
    }

    fun sendHelp(sendHelp: SendHelp) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val user = apiService.sendHelp(sendHelp)
        if (user.isSuccessful) {
            emit(Resource.success(user.body()?.toString()))
        } else {
            emit(Resource.error(user.body()?.get("status").toString()))
        }
    }

    fun getContacts(userID: UserID) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val user = apiService.getContacts(userID)
        if (user.isSuccessful) {
            emit(Resource.success(user.body()?.toString()))
        } else {
            emit(Resource.error(user.body()?.get("status").toString()))
        }
    }
}