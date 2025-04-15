package com.zhengwei.productlist.api

import android.util.Log
import com.zhengwei.productlist.BuildConfig
import com.zhengwei.productlist.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ProductApi(private val client: HttpClient) {
    companion object {
        private const val TAG = "ProductApi"
        private val BASE_URL: String = BuildConfig.BASE_URL
        private val json = Json { ignoreUnknownKeys = true }
    }

    suspend fun getAll(): List<Product>? {
        return try {
            val response = client.get("$BASE_URL/products")
            if (response.status == HttpStatusCode.OK) {
                json.decodeFromString(ListSerializer(Product.serializer()), response.bodyAsText())
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch products", e)
            null
        }
    }

    suspend fun getById(id: Int): Product? {
        return try {
            val response = client.get("$BASE_URL/products/$id")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch product with id $id", e)
            null
        }
    }

    suspend fun create(product: Product): Boolean {
        return try {
            val response = client.post("$BASE_URL/products") {
                contentType(ContentType.Application.Json)
                setBody(product)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create product", e)
            false
        }
    }

    suspend fun update(product: Product): Boolean {
        return try {
            val response = client.put("$BASE_URL/products/${product.id}") {
                contentType(ContentType.Application.Json)
                setBody(product)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update product with id ${product.id}", e)
            false
        }
    }

    suspend fun delete(id: Int): Boolean {
        return try {
            val response = client.delete("$BASE_URL/products/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete product with id $id", e)
            false
        }
    }
}