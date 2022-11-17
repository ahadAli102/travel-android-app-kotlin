package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahad.travelapp.R
import com.ahad.travelapp.adapter.UserPostAdapter
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.util.PostAction
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main_user_post.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainUserPostFragment : Fragment() {
    private lateinit var postAdapter: UserPostAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var allPosts:MutableList<Post>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_user_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        userPostAdd.setOnClickListener{
            val action = MainUserPostFragmentDirections.actionMainUserPostFragmentToMainAddPostFragment()
            Navigation.findNavController(it).navigate(action)
        }
        setupRecyclerView()
        postAdapter.setOnItemClickListener { post, postAction ->
            if(postAction == PostAction.SHOW_POST){
                val action = MainUserPostFragmentDirections.actionMainUserPostFragmentToMainSinglePostFragment(post)
                Navigation.findNavController(view).navigate(action)
            } else if (postAction == PostAction.DELETE_POST){
                mainViewModel.deletePost(post)
            }
        }
        mainViewModel.userPostResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    userPostErrorMessage.visibility = View.GONE
                    Log.d(TAG, "onViewCreated: userPostResponse loading")
                }
                is MainResponse.Success -> {
                    progressBar.visibility = View.GONE
                    userPostErrorMessage.visibility = View.GONE
                    postAdapter.differ.submitList(response.data?.toList())
                    allPosts = response.data!!
                    Log.d(TAG, "onViewCreated: userPostResponse Success ${allPosts.size}")
                    setSearchListener()
                }
                is MainResponse.Error -> {
                    progressBar.visibility = View.GONE
                    userPostErrorMessage.visibility = View.VISIBLE
                    userPostErrorMessage.text = response.message
                    Log.d(TAG, "onViewCreated: userPostResponse Success ${response.message}")
                }
            }
        })
        mainViewModel.userPostDeleteResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    Toast.makeText(context, "Deleting post", Toast.LENGTH_SHORT).show()
                }
                is MainResponse.Success -> {
                    Toast.makeText(context, response.data.toString() , Toast.LENGTH_SHORT).show()
                }
                is MainResponse.Error -> {
                    Toast.makeText(context, response.data.toString() , Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setSearchListener() {
        var job: Job? = null
        userPostSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(500)
                    newText?.let {
                        val query = newText.toString().trim()
                        val searchPosts = mutableListOf<Post>()
                        if (query.isNotEmpty()) {
                            for (post in allPosts){
                                if(post.location.startsWith(query,true)){
                                    searchPosts.add(post)
                                }
                            }
                            postAdapter.differ.submitList(searchPosts.toList())
                        }
                        else{
                            postAdapter.differ.submitList(allPosts.toList())
                        }
                    }
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        postAdapter = UserPostAdapter()
        userPostRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = postAdapter
        }
    }

    companion object {
        private const val TAG="MyTag:MainActUsPoF"
    }
}