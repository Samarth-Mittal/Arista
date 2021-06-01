package com.example.arista.data.model

class SendHelp {
    var user_id: Long = 0
    lateinit var reason: String
    lateinit var location: String

    constructor(user_id: Long, reason: String, location: String) {
        this.user_id = user_id
        this.reason = reason
        this.location = location
    }
}