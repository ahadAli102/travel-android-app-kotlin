package com.ahad.travelapp.view

import android.content.Intent
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
import kotlinx.android.synthetic.main.fragment_auth_login.*

class AuthLoginFragment : Fragment() {
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as AuthActivity).authViewModel
        Log.d(TAG, "onCreate: view model ${authViewModel.hashCode()}")

        text_view_register.setOnClickListener {
            val action = AuthLoginFragmentDirections.actionAuthLoginFragmentToAuthRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        text_view_forget_password.setOnClickListener {
            val action = AuthLoginFragmentDirections.actionAuthLoginFragmentToAuthResetPasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is AuthResponse.Success -> {
                    Log.d(TAG, "onCreate: AuthResponse.Success ${response.data}")
                    progressbar.visibility = View.GONE
                    Toast.makeText(activity, "${response.data}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity as AuthActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    (activity as AuthActivity).finish()
                }
                is AuthResponse.Loading -> {
                    Log.d(TAG, "onCreate: AuthResponse.Success")
                    progressbar.visibility = View.VISIBLE
                }
                is AuthResponse.Error -> {
                    Log.d(TAG, "onCreate: AuthResponse.Error ${response.message} ${response.data}")
                    Toast.makeText(activity, "${response.message} ${response.data}", Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.GONE
                }
            }
        })

        button_sign_in.setOnClickListener {
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
            authViewModel.login(email , password)
        }
    }

    companion object{
        private const val TAG="MyTag:LoF"
    }
}