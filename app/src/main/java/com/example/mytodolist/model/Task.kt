package com.example.mytodolist.model

data class Task(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
    val createdTime: Double,
    val deadLine: Double
)