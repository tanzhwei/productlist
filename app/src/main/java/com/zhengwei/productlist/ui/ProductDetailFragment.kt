package com.zhengwei.productlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.zhengwei.productlist.R
import com.zhengwei.productlist.api.ApiClient
import com.zhengwei.productlist.api.ProductApi
import com.zhengwei.productlist.databinding.FragmentProductDetailBinding
import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val api = ProductApi(ApiClient.httpClient)
    private val repository = ProductRepository(api)
    private val args by navArgs<ProductDetailFragmentArgs>()
    private val productId get() = args.productId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProduct()
        setHasOptionsMenu(true)
    }

    private fun loadProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            val product = repository.getProductById(productId)
            product?.let { showProduct(it) }
        }
    }

    private fun showProduct(product: Product) = with(binding) {
        textViewName.text = product.name
        textViewType.text = product.type
        textViewPrice.text = "$ %.2f".format(product.price)
        textViewDescription.text = product.description

        val pictureUrl = product.pictureUrl

        if (pictureUrl.isBlank()) {
            imageViewProduct.setImageResource(R.drawable.ic_no_image)
        } else if (pictureUrl.startsWith("content://") || pictureUrl.startsWith("file://")) {
            imageViewProduct.setImageURI(Uri.parse(pictureUrl))
        } else {
            Glide.with(imageViewProduct.context)
                .load(pictureUrl)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .fitCenter()
                .into(imageViewProduct)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_product -> {
                findNavController().navigate(
                    ProductDetailFragmentDirections.actionProductDetailFragmentToProductEditFragment(productId)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}