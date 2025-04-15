package com.zhengwei.productlist.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zhengwei.productlist.R
import com.zhengwei.productlist.databinding.ItemProductBinding
import com.zhengwei.productlist.model.Product

class ProductAdapter(
    private val onClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) = with(binding) {
            textViewName.text = product.name
            textViewPrice.text = "$ %.2f".format(product.price)
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

            root.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem
        }
    }
}