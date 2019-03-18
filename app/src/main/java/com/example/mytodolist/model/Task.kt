package com.example.mytodolist.model

data class Task(
    var id: String = "",
    var order: Int = 0,
    var title: String = "",
    var description: String = "",
    var createdTime: Double = 0.0,
    var deadLine: Double = 0.0
)

data class User(
    val email: String,
    val uid: String
)