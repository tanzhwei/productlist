package com.zhengwei.productlist.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Product(
    val id: Int = 0,
    val name: String,
    val type: String,
    val pictureUrl: String,
    val price: Double,
    val description: String
) {
    companion object {
        fun fromRow(row: ResultRow) = Product(
            id = row[Products.id].value,
            name = row[Products.name],
            type = row[Products.type],
            pictureUrl = row[Products.pictureUrl],
            price = row[Products.price],
            description = row[Products.description]
        )
    }
}

object Products : IntIdTable() {
    val name = varchar("name", 255)
    val type = varchar("type", 255)
    val pictureUrl = varchar("picture_url", 1000)
    val price = double("price")
    val description = varchar("description", 1000)
}