package com.example.eventusa.screens.tasks.model

data class Task (
    val userId: Int,
    val title: String,
    val text: String,
    val completionInPercent: Int,
)