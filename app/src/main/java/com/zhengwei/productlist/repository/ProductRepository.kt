package com.zhengwei.productlist.repository

import com.zhengwei.productlist.api.ProductApi
import com.zhengwei.productlist.model.Product

class ProductRepository(private val api: ProductApi) {

    suspend fun getProducts(): List<Product>? = api.getAll()

    suspend fun getProductById(id: Int): Product? = api.getById(id)

    suspend fun addProduct(product: Product): Boolean = api.create(product)

    suspend fun updateProduct(product: Product): Boolean = api.update(product)

    suspend fun deleteProduct(id: Int): Boolean = api.delete(id)
}