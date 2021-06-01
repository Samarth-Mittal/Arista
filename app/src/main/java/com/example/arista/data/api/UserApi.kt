package com.example.arista.data.api

import com.example.arista.data.model.LoginUser
import com.example.arista.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface  UserApi {

    @POST("/login")
    suspend fun doLogin(@Body loginUser: LoginUser): Response<HashMap<String, Any>>

    @POST("/signup")
    suspend fun doRegister(@Body signUpUser: SignUpUser): Response<HashMap<String, Any>>

    @POST("/add")
    suspend fun addContact(@Body addContact: AddContact): Response<HashMap<String, Any>>

    @POST("/help")
    suspend fun sendHelp(@Body sendHelp: SendHelp): Response<HashMap<String, Any>>

    @GET("/contacts")
    suspend fun getContacts(@Body userID: UserID): Response<HashMap<String, Any>>
}