package com.example.tastybites.ui.FoodList

data class Food(
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    var isFavorite: Boolean = false
)
