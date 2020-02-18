package com.graphql.db

import com.graphql.model.User
import com.graphql.service.CrudUserStorage
import com.graphql.table.UserTable
import com.graphql.table.UserTable.id
import org.jetbrains.squash.connection.DatabaseConnection
import org.jetbrains.squash.connection.transaction
import org.jetbrains.squash.dialects.h2.H2Connection
import org.jetbrains.squash.expressions.count
import org.jetbrains.squash.expressions.eq
import org.jetbrains.squash.schema.create
import org.jetbrains.squash.query.from
import org.jetbrains.squash.query.select
import org.jetbrains.squash.query.where
import org.jetbrains.squash.results.ResultRow
import org.jetbrains.squash.results.get
import org.jetbrains.squash.statements.*


class DbRepository(
    val db: DatabaseConnection = H2Connection.createMemoryConnection(catalogue = "DB_CLOSE_DELAY=-1")
) : CrudUserStorage {

    init {
        db.transaction {
            databaseSchema().create(UserTable)
        }
    }

    override fun addUser(user: User): Int {
        val id = db.transaction {
            insertInto(UserTable).values {
                it[username] = user.username
                it[bcryptedPassword] = user.bcryptedPassword
                it[email] = user.email
                it[uuid] = user.uuid
            }.fetch(id).execute()
        }

        return id
    }

    override fun removeUser(userId: Int) = db.transaction {
            deleteFrom(UserTable).where { UserTable.id eq userId }.execute()
    }

    override fun getUser(userId: Int): User? = db.transaction {
        val row = from(UserTable).where { UserTable.id eq userId }.execute().singleOrNull()
        row?.toUser()
    }

    override fun updateUser(user: User) {
        db.transaction {
            update(UserTable).set {
                it[username] = user.username
                it[bcryptedPassword] = user.bcryptedPassword
                it[email] = user.email
                it[uuid] = user.uuid
            }.where { UserTable.id eq user.id }.execute()
        }
    }

    //todo add the validation for parsing header
    fun validateUser(uuid: String) {
        db.transaction {
            from(UserTable)
                .select { UserTable.id.count() }
                .where { UserTable.uuid eq uuid }
                .execute().single().get<Int>(0) != 0
        }
    }
}

fun ResultRow.toUser() = User(
    id = this[UserTable.id],
    username = this[UserTable.username],
    bcryptedPassword = this[UserTable.bcryptedPassword],
    email = this[UserTable.email],
    uuid = this[UserTable.uuid]
)