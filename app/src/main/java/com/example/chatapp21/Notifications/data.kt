package com.example.chatapp21.Notifications

class data(
    val user: String,
    val icon: Int,
    val title: String, val body: String, val sented: String
) {

    constructor() : this("", 0, "", "", "")
}