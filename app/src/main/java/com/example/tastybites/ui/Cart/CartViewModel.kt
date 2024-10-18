package com.example.tastybites.ui.Cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tastybites.ui.FoodList.Food
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("cartItems")

    init {
        fetchCartItems()
    }

    private fun fetchCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartItemsList = mutableListOf<CartItem>()
                    for (snapshot in dataSnapshot.children) {
                        snapshot.getValue(CartItem::class.java)?.let { cartItemsList.add(it) }
                    }
                    _cartItems.postValue(cartItemsList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                   //TODO:Write extra cases if needed
                }
            })
        }
    }

    fun addToCart(food: Food) {
        val currentItems = _cartItems.value ?: emptyList()
        val existingItem = currentItems.find { it.food?.id == food.id }

        if (existingItem != null) {
            existingItem.quantity++
            updateCartItemInFirebase(existingItem)
        } else {
            val newItem = CartItem(food, 1)
            _cartItems.value = currentItems + newItem
            addCartItemToFirebase(newItem)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentItems = _cartItems.value ?: emptyList()

        val updatedItems = currentItems.map { item ->
            if (item.food?.id == cartItem.food?.id) {
                item.copy(quantity = item.quantity - 1)
            } else {
                item
            }
        }.filter { it.quantity > 0 }

        _cartItems.value = updatedItems
        syncCartItemsToFirebase(updatedItems)
    }

    private fun addCartItemToFirebase(cartItem: CartItem) {
        val cartItemId = databaseReference.push().key ?: return
        databaseReference.child(cartItemId).setValue(cartItem)
    }

    private fun updateCartItemInFirebase(cartItem: CartItem) {
        val query = databaseReference.orderByChild("food/id").equalTo(cartItem.food?.id).limitToFirst(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.setValue(cartItem)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //TODO:Write extra cases if needed
            }
        })
    }

    private fun syncCartItemsToFirebase(updatedItems: List<CartItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            databaseReference.removeValue()
            updatedItems.forEach { addCartItemToFirebase(it) }
        }
    }
}
