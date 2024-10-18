package com.example.tastybites.ui.FoodList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class FoodListViewModel : ViewModel() {

    private val _foodList = MutableLiveData<List<Food>>()
    val foodList: LiveData<List<Food>> = _foodList

    val _favoritesList = MutableLiveData<List<Food>>()
    val favoritesList: LiveData<List<Food>> = _favoritesList

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("foodItems")

    init {
        fetchFoodItems()
    }

    private fun fetchFoodItems() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foods = mutableListOf<Food>()

                for (child in snapshot.children) {
                    val food = child.getValue(Food::class.java)
                    if (food != null) {
                        foods.add(food)
                    } else {
                        Log.w("FoodListViewModel", "error aaya bhai fetch pe 25 snapshot mai issue hai")
                    }
                }

                Log.d("FoodListViewModel", "Fetched ${foods.size} items.")
                _foodList.postValue(foods)
                refreshFavorites(foods)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FoodListViewModel", "cache exception check kr : ${error.message}")
            }
        })
    }

    private fun refreshFavorites(foodList: List<Food>) {
        val favoriteItems = foodList.filter { it.isFavorite }
        _favoritesList.value = favoriteItems
    }

    fun updateFoodInFirebase(food: Food) {
        val foodId = food.id ?: return

        databaseReference.child(foodId).setValue(food)
            .addOnSuccessListener {
                Log.d("FoodListViewModel", "real time jaake update check kr: ${food.name}")
                val updatedFoodList = _foodList.value?.map {
                    if (it.id == food.id) food else it
                } ?: listOf()

                _foodList.postValue(updatedFoodList)
                refreshFavorites(updatedFoodList)
            }
            .addOnFailureListener { e ->
                Log.e("FoodListViewModel", "cache exception check kr : ${e.message}")
            }
    }
}
