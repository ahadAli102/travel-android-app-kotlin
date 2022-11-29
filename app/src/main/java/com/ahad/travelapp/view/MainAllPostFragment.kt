package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ahad.travelapp.R
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main_all_post.*

class MainAllPostFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private var firstLoading:Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_all_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        mainViewModel.allPostResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    if (firstLoading) {
                        Log.d(TAG, "onViewCreated: loading")
                    } else {
                        Log.d(TAG, "onViewCreated: paginating")
                    }
                }
                is MainResponse.Success -> {
                    Log.d(TAG, "onViewCreated: ")
                    Log.d(TAG, "onViewCreated: ")
                    Log.d(TAG, "onViewCreated: ")
                    Log.d(TAG, "onViewCreated: ${response.data?.size}")
                    for (post in response.data!!) {
                        Log.d(TAG, "onViewCreated: ${post.id}---${post.time}    ${post.user.uId}---${post.user.name}")
                    }
                    if (response.data.size < 11)
                        mainViewModel.loadAllPost()
                }
                is MainResponse.Error -> {
                    Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        private const val TAG = "MyTag:AllPostFragment"
    }
}