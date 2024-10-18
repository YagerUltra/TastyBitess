package com.example.tastybites.ui.FoodList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tastybites.databinding.ItemFoodBinding
import com.google.firebase.database.FirebaseDatabase

class FoodListAdapter(
    private var foodList: List<Food>,
    private val onAddToCartClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("foodItems")

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvFoodName.text = food.name
            binding.tvFoodPrice.text = "Price: ₹${food.price}"

            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(food)
            }

            binding.btnFavorite.isChecked = food.isFavorite

            binding.btnFavorite.setOnCheckedChangeListener { _, isChecked ->
                food.isFavorite = isChecked
                updateFavoriteStatusInFirebase(food)
            }
        }

        private fun updateFavoriteStatusInFirebase(food: Food) {
            food.id?.let {
                databaseReference.child(it).setValue(food)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount() = foodList.size

    fun updateData(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}
