package com.example.mytodolist.model

data class TodoTask(
    var id: String = "",
    var order: Int = 0,
    var title: String = "",
    var description: String = "",
    var createdTime: Double = 0.0,
    var deadLine: Double = 0.0,
    var done: Boolean = false,
    var nextID: String = ""
)

data class User(
    val email: String,
    val uid: String
)