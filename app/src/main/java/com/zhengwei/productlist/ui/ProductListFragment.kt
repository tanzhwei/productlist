package com.zhengwei.productlist.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zhengwei.productlist.R
import com.zhengwei.productlist.adapter.ProductAdapter
import com.zhengwei.productlist.databinding.FragmentProductListBinding
import com.zhengwei.productlist.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupSearchBar()
        setupSortSpinner()
        observeProducts()

        val savedStateHandle = findNavController()
            .getBackStackEntry(R.id.productListFragment)
            .savedStateHandle
        Log.d("ProductListFragment", "savedStateHandle: $savedStateHandle")
        savedStateHandle.getLiveData<Boolean>("shouldRefresh")
            .observe(viewLifecycleOwner) { shouldRefresh ->
                Log.d("ProductListFragment", "Should refresh: $shouldRefresh")
                if (shouldRefresh) {
                    viewModel.refreshProducts()
                    savedStateHandle.remove<Boolean>("shouldRefresh")
                }
            }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            val navController = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)
                ?.findNavController()

            navController?.navigate(
                ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(product.id)
            )
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupSortSpinner() {
        val options = listOf("Filter", "Price ↑", "Price ↓", "Type")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = spinnerAdapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setSortOption(options[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun observeProducts() {
        lifecycleScope.launch {
            viewModel.getProducts().collectLatest {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                findNavController().navigate(
                    ProductListFragmentDirections.actionProductListFragmentToProductEditFragment(productId = 0)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}