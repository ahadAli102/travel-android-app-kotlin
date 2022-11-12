package com.ahad.travelapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ahad.travelapp.R
import kotlinx.android.synthetic.main.fragment_auth_login.*

class AuthLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_sign_in.setOnClickListener {

        }

        text_view_register.setOnClickListener {
            val action = AuthLoginFragmentDirections.actionAuthLoginFragmentToAuthRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        text_view_forget_password.setOnClickListener {
            val action = AuthLoginFragmentDirections.actionAuthLoginFragmentToAuthResetPasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
}