package com.avgust.appnest.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val following: MutableList<String> = mutableListOf(),
    val bio: String = "",
    val imageUrl: String = ""
)