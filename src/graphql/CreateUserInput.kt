package com.graphql.graphql

import com.graphql.model.User
import java.util.*

data class CreateUserInput (
    val id: Int = 0,
    val username: String = "",
    val bcryptedPassword: String = "",
    var email: String = "",
    var uuid: String = UUID.randomUUID().toString()
)

fun CreateUserInput.toUser() : User {
    return User(
        id = this.id,
        username = this.username,
        bcryptedPassword = this.bcryptedPassword,
        email = this.email,
        uuid = this.uuid
    )
}