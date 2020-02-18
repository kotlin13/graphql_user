package com.graphql.graphql

import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.schema.Schema
import com.graphql.model.User
import com.graphql.service.CrudUserStorage
import kotlin.random.Random

class GraphQLSchemaBuilder(private val crudUserStorage: CrudUserStorage)  {

    companion object {
        private val MSG_SUCCESS = "OK"
    }

    //KGraphQL#schema { } is entry point to create KGraphQL schema
    fun kGraphqlSchema() = KGraphQL.schema {

        //configure method allows you customize schema behaviour
        configure {
            useDefaultPrettyPrinter = true
        }

        query("user") {
            description = "Returns a single User based on the id"
            resolver { id: Int -> crudUserStorage.getUser(id) ?: throw AppException("User with id: $id does not exist") }
        }

        mutation("createUser") {
            description = "Add a new user"
            resolver { input: CreateUserInput -> crudUserStorage.addUser(input.toUser()) }
        }

        mutation("removeUser") {
            description = "Remove user by Id"
            resolver { id: Int -> crudUserStorage.removeUser(id)
                "OK"
            }
        }

        mutation("updateUser") {
            description = "Update user"
            resolver { input: CreateUserInput -> crudUserStorage.updateUser(input.toUser())
              "OK"
            }
        }

        inputType<CreateUserInput>()

        //kotlin classes need to be registered with "type" method
        type<User>()
    }

    init {
        val admin = CreateUserInput(username = "mock@mock.com")
        crudUserStorage.addUser(admin.toUser())
    }
}