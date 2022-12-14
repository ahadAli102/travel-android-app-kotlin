package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahad.travelapp.R
import com.ahad.travelapp.adapter.AllPostAdapter
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main_all_post.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainAllPostFragment : Fragment() {
    private lateinit var postAdapter: AllPostAdapter
    private lateinit var mainViewModel: MainViewModel
    private var firstLoading:Boolean = true
    private var reachedEnd:Boolean = false
    private var postSize = 0
    private var isSearchedPosts:Boolean = false
    private lateinit var posts:MutableList<Post>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_all_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        postAdapter.setOnItemClickListener { post, postAction ->
            val action = MainAllPostFragmentDirections.actionMainAllPostFragmentToMainSinglePostFragment(post)
            Navigation.findNavController(view).navigate(action)
        }
        mainViewModel = (activity as MainActivity).mainViewModel
        mainViewModel.allPostResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    if (firstLoading) {
                        firstProgressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onViewCreated: loading")
                    } else {
                        paginateProgressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onViewCreated: paginating")
                    }
                }
                is MainResponse.Success -> {
                    response.data?.let {
                        firstLoading = false
                        firstProgressBar.visibility = View.GONE
                        paginateProgressBar.visibility = View.GONE
                        reachedEnd = postSize == it.size
                        postSize = it.size
                        postAdapter.differ.submitList(it.toList())
                        posts = it
                    }
                }
                is MainResponse.Error -> {
                    Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT).show()
                    firstProgressBar.visibility = View.GONE
                    paginateProgressBar.visibility = View.GONE
                }
            }
            isSearchedPosts = false
            setSearchListener()
        })
        mainViewModel.searchedPostResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    firstProgressBar.visibility = View.VISIBLE
                }
                is MainResponse.Success -> {
                    response.data?.let {
                        firstProgressBar.visibility = View.GONE
                        paginateProgressBar.visibility = View.GONE
                        reachedEnd = postSize == it.size
                        postSize = it.size
                        postAdapter.differ.submitList(it.toList())
                    }
                }
                is MainResponse.Error -> {
                    Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT).show()
                    firstProgressBar.visibility = View.GONE
                }
            }
            isSearchedPosts = true
        })
    }

    private fun setupRecyclerView() {
        postAdapter = AllPostAdapter()
        allPostRecycler.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(myScrollListener)
        }
    }

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false

    private val myScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isScrolling && !reachedEnd && !isSearchedPosts

            if(shouldPaginate) {
                mainViewModel.loadAllPost()
                isScrolling = false
            } else {
                allPostRecycler.setPadding(0, 0, 0, 0)
            }

            if(isAtLastItem){
                Log.d(TAG, "onScrolled: last visible:${firstVisibleItemPosition + visibleItemCount} " +
                        "- total count:$totalItemCount - at end:$isAtLastItem")
            }
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun setSearchListener() {
        var job: Job? = null
        allPostSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(500)
                    newText?.let {
                        val query = newText.toString().trim()
                        if (query.isNotEmpty()) {
                            mainViewModel.searchPost(query)
                        }
                        else{
                            postAdapter.differ.submitList(posts.toList())
                            isSearchedPosts = false
                        }
                    }
                }
                return true
            }
        })
    }

    companion object {
        private const val TAG = "MyTag:AllPostFragment"
    }
}