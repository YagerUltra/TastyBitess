package com.example.tastybites.ui.Favourite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tastybites.databinding.ItemFoodBinding
import com.example.tastybites.ui.FoodList.Food

class FavoriteListAdapter(
    private val favoriteList: List<Food>,
    private val onRemoveFavoriteClick: (Food) -> Unit
) : RecyclerView.Adapter<FavoriteListAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(food: Food) {
            binding.tvFoodName.text = food.name
            binding.tvFoodPrice.text = "Price: ₹${food.price}"

            binding.btnFavorite.isChecked = true

            binding.btnFavorite.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) onRemoveFavoriteClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount() = favoriteList.size
}
