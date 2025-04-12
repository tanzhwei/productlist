package com.zhengwei.productlist.di

import com.zhengwei.productlist.repository.ProductRepository
import com.zhengwei.productlist.service.ProductService
import org.koin.dsl.module

val appModule = module {
    single { ProductRepository() }
    single { ProductService(get()) }
}