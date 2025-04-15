package com.zhengwei.productlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhengwei.productlist.api.ApiClient
import com.zhengwei.productlist.api.ProductApi
import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val api = ProductApi(ApiClient.httpClient)
    private val repository = ProductRepository(api)

    private val _searchQuery = MutableStateFlow("")
    private val _sortOption = MutableStateFlow("Filter")

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 0)

    init {
        viewModelScope.launch {
            combine(_searchQuery, _sortOption, _refreshTrigger.onStart { emit(Unit) } ) { query, sort, _ -> query to sort }
                .collectLatest { (query, sort) ->
                    val result = repository.getProducts().orEmpty()
                        .filter { it.name.contains(query, ignoreCase = true) }
                        .let { list ->
                            when (sort) {
                                "Price ↑" -> list.sortedBy { it.price }
                                "Price ↓" -> list.sortedByDescending { it.price }
                                "Type" -> list.sortedBy { it.type }
                                else -> list
                            }
                        }
                    _products.value = result
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(option: String) {
        _sortOption.value = option
    }

    fun getProducts(): StateFlow<List<Product>> = _products.asStateFlow()

    fun refreshProducts() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }
}