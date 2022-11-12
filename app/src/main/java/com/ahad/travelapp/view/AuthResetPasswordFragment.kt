package com.ahad.travelapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ahad.travelapp.R
import kotlinx.android.synthetic.main.fragment_auth_reset_password.*

class AuthResetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_login.setOnClickListener {
            val action = AuthResetPasswordFragmentDirections.actionAuthResetPasswordFragmentToAuthLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
}