package com.example.tastybites.ui.Favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastybites.databinding.FragmentFavouriteBinding
import com.example.tastybites.ui.FoodList.Food
import com.example.tastybites.ui.FoodList.FoodListViewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodListViewModel: FoodListViewModel
    private lateinit var favoriteListAdapter: FavoriteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        foodListViewModel = ViewModelProvider(requireActivity())[FoodListViewModel::class.java]

        binding.rvFavoriteList.layoutManager = LinearLayoutManager(requireContext())

        foodListViewModel.favoritesList.observe(viewLifecycleOwner) { favoriteList ->
            favoriteListAdapter = FavoriteListAdapter(favoriteList) { food ->
                removeFromFavorites(food)
            }
            binding.rvFavoriteList.adapter = favoriteListAdapter
        }

        return binding.root
    }

    private fun removeFromFavorites(food: Food) {
        food.isFavorite = false
        foodListViewModel.updateFoodInFirebase(food)
        refreshFavoritesList(food)
    }

    private fun refreshFavoritesList(food: Food) {
        val updatedFavorites = foodListViewModel.favoritesList.value?.toMutableList()
        updatedFavorites?.remove(food)
        foodListViewModel._favoritesList.value = updatedFavorites
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
