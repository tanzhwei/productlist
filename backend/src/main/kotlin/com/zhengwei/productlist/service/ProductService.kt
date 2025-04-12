package com.zhengwei.productlist.service

import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.repository.ProductRepository

class ProductService(private val repository: ProductRepository) {

    fun getAllProducts(): List<Product> = repository.getAll()

    fun getProductById(id: Int): Product? = repository.getById(id)

    fun addProduct(product: Product): Product = repository.create(product)

    fun updateProduct(id: Int, product: Product): Boolean = repository.update(id, product)

    fun deleteProduct(id: Int): Boolean = repository.delete(id)
}