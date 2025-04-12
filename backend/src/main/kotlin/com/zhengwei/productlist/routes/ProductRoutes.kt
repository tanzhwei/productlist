package com.zhengwei.productlist.routes

import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.service.ProductService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject


fun Route.registerProductRoutes() {
    val productService by inject<ProductService>()

    route("/products") {

        get {
            call.respond(productService.getAllProducts())
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val product = productService.getProductById(id)
            if (product == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(product)
            }
        }

        post {
            val product = call.receive<Product>()
            call.respond(HttpStatusCode.Created, productService.addProduct(product))
        }

        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }

            val updatedProduct = call.receive<Product>()
            val success = productService.updateProduct(id, updatedProduct)

            if (success) {
                call.respond(HttpStatusCode.OK, "Product updated")
            } else {
                call.respond(HttpStatusCode.NotFound, "Product not found")
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null || !productService.deleteProduct(id)) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}