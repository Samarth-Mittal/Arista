package com.example.arista.ui.main.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.arista.data.model.LoginUser
import com.example.arista.data.api.ApiClient
import com.example.arista.data.api.UserApi
import com.example.arista.data.model.SignUpUser
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

    fun dofunc() = liveData(Dispatchers.IO) {
        print("acsfd")
        emit(Resource.loading())
        val signup_response = apiService.func()
        if (signup_response.isSuccessful) {
            emit(Resource.success(signup_response.body()))
        } else {
            emit(Resource.error(signup_response.body().toString()))
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

    fun addContact(id: Int, name: String, number: String, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val user = apiService.addContact(id, name, number)
        if (user.isSuccessful) {
            val editor = token.edit()
            editor.putString("isProfileSet", "Yes")
            editor.commit()
            emit(Resource.success(user.body()?.toString()))
        } else {
            emit(Resource.error(user.body()?.get("status").toString()))
        }
    }
}