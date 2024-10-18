package com.example.tastybites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.widget.EditText

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if the first name has already been stored
        if (sharedPreferences.getString("firstName", null) == null) {
            showFirstNameDialog()
        } else {
            proceedToMainActivity()
        }
    }

    private fun showFirstNameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialoug_first_name, null)
        val firstNameEditText = dialogView.findViewById<EditText>(R.id.editTextFirstName)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Welcome Foodie Let Get Started !!")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val firstName = firstNameEditText.text.toString()
                storeFirstName(firstName)
                proceedToMainActivity()
            }
            .setNegativeButton("Cancel") { _, _ -> finish() } // Close app if canceled
            .create()

        dialog.show()
    }

    private fun storeFirstName(firstName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("firstName", firstName)
        editor.apply()
    }

    private fun proceedToMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
