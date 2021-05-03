package com.example.arista.data.api

import com.example.arista.data.model.LoginUser
import com.example.arista.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface  UserApi {

    @POST(" /login")
    suspend fun doLogin(@Body loginUser: LoginUser): Response<HashMap<String, Any>>

    @POST("/registration")
    suspend fun doRegister(@Body signUpUser: SignUpUser): Response<HashMap<String, Any>>

    @POST("/add")
    suspend fun addContact(@Body id: Int, name: String, number: String): Response<HashMap<String, Any>>

    @POST("/help")
    suspend fun sendHelp(@Body id: Long, location: String): Response<HashMap<String, Any>>

    @GET("/qwerty")
    suspend fun func(): Response<String>
}