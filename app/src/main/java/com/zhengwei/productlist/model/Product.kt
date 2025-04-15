package com.zhengwei.productlist.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val type: String,
    val pictureUrl: String,
    val price: Double,
    val description: String
)