package com.graphql.table

import org.jetbrains.squash.definition.*

object UserTable : TableDefinition() {
    val id = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 128)
    val bcryptedPassword = varchar("bcryptedPassword", 128)
    val email = varchar("email", 128)
    //todo check for the future
    val uuid = varchar("uuid", 50).index()
}

