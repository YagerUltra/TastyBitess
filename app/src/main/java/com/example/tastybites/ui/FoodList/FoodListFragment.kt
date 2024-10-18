package com.example.tastybites.ui.FoodList

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastybites.R
import com.example.tastybites.databinding.FragmentFooodlistBinding
import com.example.tastybites.ui.Cart.CartViewModel

class FoodListFragment : Fragment() {

    private var _binding: FragmentFooodlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodListViewModel: FoodListViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var foodListAdapter: FoodListAdapter
    private lateinit var loadingView: View
    private lateinit var emptyView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFooodlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        foodListViewModel = ViewModelProvider(this)[FoodListViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        foodListAdapter = FoodListAdapter(emptyList()) { foodItem ->
            cartViewModel.addToCart(foodItem)
            Toast.makeText(requireContext(), "${foodItem.name} added to cart!", Toast.LENGTH_SHORT)
                .show()
        }

        binding.rvFoodList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodListAdapter
        }

        loadingView = inflater.inflate(R.layout.loading_screen, container, false)
        emptyView = inflater.inflate(R.layout.loading_screen, container, false)

        checkInternetAndLoadData()


        return root
    }



    private fun checkInternetAndLoadData() {
        if (isInternetAvailable()) {
            loadData()
        } else {
            binding.rvFoodList.visibility = View.GONE
            showLoadingScreen()

            Handler().postDelayed({
                if (isInternetAvailable()) {
                    loadData()
                } else {
                    showEmptyScreen()
                }
            }, 5000)
        }
    }

    private fun loadData() {
        foodListViewModel.foodList.observe(viewLifecycleOwner) { foodList ->
            foodListAdapter.updateData(foodList)
            hideLoadingScreen()
            binding.rvFoodList.visibility = View.VISIBLE
        }
    }

    private fun showLoadingScreen() {
        binding.rvFoodList.visibility = View.GONE
        val fragmentContainer = binding.root as ViewGroup
        fragmentContainer.addView(loadingView)
    }

    private fun hideLoadingScreen() {
        if (loadingView.parent != null) {
            (loadingView.parent as ViewGroup).removeView(loadingView)
        }
    }

    private fun showEmptyScreen() {
        binding.rvFoodList.visibility = View.GONE
        val fragmentContainer = binding.root as ViewGroup
        fragmentContainer.addView(emptyView)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
