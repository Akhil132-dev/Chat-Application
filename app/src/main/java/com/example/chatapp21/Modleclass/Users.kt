package com.example.chatapp21.Modleclass

import java.lang.reflect.Constructor


class Users(
    val uid: String,
    val Username: String,
    val ProfileImage: String,
    val cover: String,
    val Status: String,
    val search: String,
    val facebook: String,
    val instagram: String,
    val website: String

){
    constructor():this("","","","","","","","","")
}