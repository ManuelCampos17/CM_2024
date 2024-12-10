package com.example.hogwartshoppers.model

import kotlinx.serialization.Serializable

@Serializable
data class Friends(
    val username: String,
    val friends: List<String>
)