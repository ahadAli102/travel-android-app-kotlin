package com.ahad.travelapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ahad.travelapp.R
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_edit_user_name.*

class EditUserNameFragment (private val user: User,private val mainViewModel: MainViewModel) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.userNameInsertResponse.observe(viewLifecycleOwner,{ response->
            when (response) {
                is MainResponse.Success -> {
                    Toast.makeText(context, "${response.data}", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is MainResponse.Error -> {
                    editUserNameMessage.text = response.data
                }
                is MainResponse.Loading -> {
                    editUserNameMessage.text = "Updating user name"
                }

            }
        })
        editUserNameSubmit.setOnClickListener {
            val name = editUserNameText.text.toString()
            if(name.isEmpty() || name.isBlank()){
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
            }else{
                mainViewModel.saveUserName(name, user)
            }
        }
    }
}