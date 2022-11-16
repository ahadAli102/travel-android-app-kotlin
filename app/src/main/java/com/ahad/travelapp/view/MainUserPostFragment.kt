package com.ahad.travelapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.ahad.travelapp.R
import com.ahad.travelapp.model.Post
import kotlinx.android.synthetic.main.fragment_main_user_post.*

class MainUserPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_user_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPostAdd.setOnClickListener{
            val action = MainUserPostFragmentDirections.actionMainUserPostFragmentToMainAddPostFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    companion object {
        private const val TAG="MyTag:MainActUsPoF"
    }
}