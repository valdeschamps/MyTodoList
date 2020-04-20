package com.valent.mytodolist.model

data class TodoTask(
    var id: String = "",
    var title: String = "",
    var details: String = "",
    var createdTime: Double = 0.0,
    var dateTimestamp: Long = -1L,
    var timeTimestamp: Long = -1L,
    var done: Boolean = false,
    var nextID: String = ""
)

data class User(
    val email: String,
    val uid: String
)