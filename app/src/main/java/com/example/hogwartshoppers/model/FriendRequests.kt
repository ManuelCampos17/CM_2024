package com.example.hogwartshoppers.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequests(
    val username1: String,
    val username2: String
)