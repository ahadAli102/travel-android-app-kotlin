package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.ahad.travelapp.R
import com.ahad.travelapp.util.AuthResponse
import com.ahad.travelapp.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.fragment_auth_reset_password.*
import kotlinx.android.synthetic.main.fragment_auth_reset_password.progressbar

class AuthResetPasswordFragment : Fragment() {
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as AuthActivity).authViewModel
        Log.d(TAG, "onCreate: view model ${authViewModel.hashCode()}")
        button_login.setOnClickListener {
            val action = AuthResetPasswordFragmentDirections.actionAuthResetPasswordFragmentToAuthLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }

        authViewModel.resetPassResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is AuthResponse.Success -> {
                    progressbar.visibility = View.GONE
                    Toast.makeText(activity, "${response.data} reset password with email", Toast.LENGTH_SHORT).show()
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

        button_reset_password.setOnClickListener {
            val email = edit_text_email.text.toString().trim()
            authViewModel.resetPassword(email)
        }

    }

    companion object{
        private const val TAG="MyTag:RePsF"
    }
}