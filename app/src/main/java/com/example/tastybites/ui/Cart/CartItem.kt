package com.example.tastybites.ui.Cart

import com.example.tastybites.ui.FoodList.Food

data class CartItem(
    var food: Food? = null,
    var quantity: Int = 0
)
