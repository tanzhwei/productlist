package com.zhengwei.productlist.repository

import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.model.Products
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProductRepository {

    fun getAll(): List<Product> = transaction {
        Products.selectAll().map { Product.fromRow(it) }
    }

    fun getById(id: Int): Product? = transaction {
        Products.select { Products.id eq id }
            .mapNotNull { Product.fromRow(it) }
            .singleOrNull()
    }

    fun create(product: Product): Product = transaction {
        val id = Products.insertAndGetId {
            it[name] = product.name
            it[type] = product.type
            it[pictureUrl] = product.pictureUrl
            it[price] = product.price
            it[description] = product.description
        }.value
        product.copy(id = id)
    }

    fun update(id: Int, product: Product): Boolean = transaction {
        Products.update({ Products.id eq id }) {
            it[name] = product.name
            it[type] = product.type
            it[pictureUrl] = product.pictureUrl
            it[price] = product.price
            it[description] = product.description
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        Products.deleteWhere { Products.id eq id } > 0
    }
}
