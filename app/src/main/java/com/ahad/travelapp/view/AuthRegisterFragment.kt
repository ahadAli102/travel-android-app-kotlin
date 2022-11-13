package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.ahad.travelapp.R
import com.ahad.travelapp.util.AuthResponse
import com.ahad.travelapp.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.fragment_auth_register.*
import kotlinx.android.synthetic.main.fragment_auth_register.edit_text_email
import kotlinx.android.synthetic.main.fragment_auth_register.edit_text_password
import kotlinx.android.synthetic.main.fragment_auth_register.progressbar

class AuthRegisterFragment : Fragment() {
    lateinit var authViewModel:AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as AuthActivity).authViewModel
        Log.d(TAG, "onCreate: view model ${authViewModel.hashCode()}")
        text_view_login.setOnClickListener {
            val action = AuthRegisterFragmentDirections.actionAuthRegisterFragmentToAuthLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }

        authViewModel.regResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is AuthResponse.Success -> {
                    progressbar.visibility = View.GONE
                    Toast.makeText(activity, "${response.data} go to login", Toast.LENGTH_SHORT).show()
                }
                is AuthResponse.Loading -> {
                    progressbar.visibility = View.VISIBLE
                }
                is AuthResponse.Error -> {
                    Toast.makeText(activity, "${response.message} ${response.data}", Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.GONE
                }
            }
        })

        button_register.setOnClickListener {
            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if (email.isEmpty()) {
                edit_text_email.error = "Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.error = "Valid Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                edit_text_password.error = "6 char password required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }
            authViewModel.register(email, password)
        }
    }
    companion object{
        private const val TAG="MyTag:ReF"
    }
}