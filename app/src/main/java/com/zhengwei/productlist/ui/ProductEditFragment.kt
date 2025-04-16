package com.zhengwei.productlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.zhengwei.productlist.R
import com.zhengwei.productlist.api.ApiClient
import com.zhengwei.productlist.api.ProductApi
import com.zhengwei.productlist.databinding.FragmentProductEditBinding
import com.zhengwei.productlist.model.Product
import com.zhengwei.productlist.repository.ProductRepository
import kotlinx.coroutines.launch
import java.io.File

class ProductEditFragment : Fragment() {

    private var _binding: FragmentProductEditBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ProductEditFragmentArgs>()
    private val productId get() = args.productId
    private val api = ProductApi(ApiClient.httpClient)
    private val repository = ProductRepository(api)
    private var imageUri: Uri? = null
    private var pictureUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        if (productId > 0) {
            loadProduct()
        }

        binding.buttonUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun loadProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (productId > 0) {
                val product = repository.getProductById(productId)
                product?.let { fillForm(it) }
            }
        }
    }

    private fun fillForm(product: Product) = with(binding) {
        editTextName.setText(product.name)
        editTextType.setText(product.type)
        editTextPrice.setText(product.price.toString())
        editTextDescription.setText(product.description)
        pictureUrl = product.pictureUrl

        if (pictureUrl.isBlank()) {
            binding.imagePreview.setImageResource(R.drawable.ic_no_image)
        } else if (pictureUrl.startsWith("content://") || pictureUrl.startsWith("file://")) {
            binding.imagePreview.setImageURI(Uri.parse(pictureUrl))
        } else {
            Glide.with(binding.imagePreview.context)
                .load(pictureUrl)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .fitCenter()
                .into(binding.imagePreview)
        }
    }

    private fun saveProduct() {
        val name = binding.editTextName.text.toString()
        val type = binding.editTextType.text.toString()
        val price = binding.editTextPrice.text.toString().toDoubleOrNull()
        val description = binding.editTextDescription.text.toString()

        if (name.isBlank() || type.isBlank() || price == null || description.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            id = productId,
            name = name,
            type = type,
            price = price,
            description = description,
            pictureUrl = pictureUrl
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val success = if (productId > 0) {
                repository.updateProduct(product)
            } else {
                repository.addProduct(product)
            }
            if (success) {
                val message = if (productId > 0) "Product updated successfully" else "Product added successfully"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                val message = if (productId > 0) "Failed to update product" else "Failed to add product"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            findNavController()
                .getBackStackEntry(R.id.productListFragment)
                .savedStateHandle["shouldRefresh"] = true
            findNavController().popBackStack()
        }
    }

    private fun deleteProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            val success = repository.deleteProduct(productId)
            val message = if (success) "Product deleted successfully" else "Failed to delete product"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            findNavController()
                .getBackStackEntry(R.id.productListFragment)
                .savedStateHandle["shouldRefresh"] = true

            findNavController().popBackStack()
            findNavController().popBackStack()
        }
    }

    private fun updatePicturePreview(url: String?) {
        if (!url.isNullOrBlank()) {
            Glide.with(binding.imagePreview.context)
                .load(url)
                .fitCenter()
                .into(binding.imagePreview)
        } else {
            binding.imagePreview.setImageDrawable(null)
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            saveImageToInternalStorage(it)?.let { savedUri ->
                imageUri = savedUri
                pictureUrl = savedUri.toString()
                binding.imagePreview.setImageURI(savedUri)
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "product_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_edit, menu)

        val deleteMenuItem = menu.findItem(R.id.action_delete_product)
        deleteMenuItem?.isVisible = productId > 0

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_product -> {
                saveProduct()
                true
            }
            R.id.action_delete_product -> {
                deleteProduct()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}