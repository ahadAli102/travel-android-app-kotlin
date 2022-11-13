package com.ahad.travelapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.ahad.travelapp.R
import com.ahad.travelapp.util.AuthResponse
import com.ahad.travelapp.viewmodel.AuthViewModel
import com.ahad.travelapp.viewmodel.AuthViewModelFactory
import kotlinx.android.synthetic.main.fragment_auth_reset_password.*

class AuthActivity : AppCompatActivity() {
    lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModelFactory = AuthViewModelFactory()
        authViewModel = ViewModelProvider(this,viewModelFactory).get(AuthViewModel::class.java)
        authViewModel.loginUserResponse.observe(this, { response ->
            when (response) {
                is AuthResponse.Success -> {
                    Toast.makeText(this@AuthActivity, "Email: ${response.data}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                is AuthResponse.Error -> {
                    Toast.makeText(this@AuthActivity, "No login ${response.message} ${response.data}", Toast.LENGTH_SHORT).show()
                }
            }
        })
        Log.d(TAG, "onCreate: view model ${authViewModel.hashCode()}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setupActionBarWithNavController(findNavController(R.id.auth_fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.auth_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object{
        private const val TAG="MyTag:AuthAct"
    }
}