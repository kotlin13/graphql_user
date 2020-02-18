package com.graphql.service

import com.graphql.model.User

interface CrudUserStorage {

    fun addUser(user: User): Int

    fun removeUser(userId: Int)

    fun getUser(userId: Int): User?

    fun updateUser(user: User)
}