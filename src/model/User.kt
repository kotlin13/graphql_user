package com.graphql.model

data class User (
    val id: Int,
    val username: String,
    val bcryptedPassword: String,
    val email: String,
    val uuid: String
)