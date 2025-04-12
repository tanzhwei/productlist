package com.zhengwei.productlist

import com.zhengwei.productlist.di.appModule
import com.zhengwei.productlist.model.Products
import com.zhengwei.productlist.routes.registerProductRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    Database.connect(
        url = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/productlist",
        driver = "org.postgresql.Driver",
        user = System.getenv("DB_USER") ?: "tanzhengwei",
        password = System.getenv("DB_PASSWORD") ?: ""
    )

    transaction {
        SchemaUtils.create(Products)
    }

    install(ContentNegotiation) {
        json()
    }

    install(Koin) {
        printLogger()
        modules(appModule)
    }

    routing {
        registerProductRoutes()
    }
}
