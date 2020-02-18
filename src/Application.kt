package com.graphql

import com.apurebase.kgraphql.schema.Schema
import com.google.gson.Gson
import com.graphql.db.DbRepository
import com.graphql.graphql.AppException
import com.graphql.graphql.GraphQLSchemaBuilder
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein
import org.slf4j.Logger

fun main(args: Array<String>): Unit {
    val port = System.getenv("port").toInt()
    embeddedServer(Netty, port) {
        kodein {
            bind<DbRepository>() with singleton { DbRepository() }
            bind<GraphQLSchemaBuilder>() with singleton { GraphQLSchemaBuilder(DbRepository()) }
            bind<Gson>() with singleton { Gson() }
        }
    module()
    getUser()
    }.start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
}

internal fun Application.getUser() {
    val gson by kodein().instance<Gson>()
    val dbRepository by kodein().instance<DbRepository>()
    val graphQLSchemaBuilder = GraphQLSchemaBuilder(dbRepository)
    routing {
        graphql(log, gson, graphQLSchemaBuilder.kGraphqlSchema())
    }
}

fun Application.graphql(log: Logger, gson: Gson, schema: Schema) {
    suspend fun ApplicationCall.executeQuery() {
        val graphQLRequest = receive<GraphQLRequest>()
        val query = graphQLRequest.query
        val userNameAuthHeader = request.header("Authorization")
        log.info("the graphql query: $query")
        val variables = gson.toJson(graphQLRequest.variables)
        log.info("the graphql variables: $variables")
        try {
            val result = schema.execute(query, variables)
            respondText(result, ContentType.Application.Json)
        } catch (e: Exception) {
            respondText(gson.toJson(e.message?.let { msg -> AppException(msg) }))
        }
    }

    routing {
        post("/graphql") {
            call.executeQuery()
        }
    }
}

data class GraphQLRequest(val query: String = "", val variables: Map<String, Any> = emptyMap())

