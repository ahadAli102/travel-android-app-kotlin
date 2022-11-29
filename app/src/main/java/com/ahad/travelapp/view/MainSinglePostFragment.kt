package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahad.travelapp.R
import com.ahad.travelapp.adapter.CommentAdapter
import com.ahad.travelapp.adapter.SinglePostImageAdapter
import com.ahad.travelapp.adapter.SinglePostVideoAdapter
import com.ahad.travelapp.model.Comment
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main_single_post.*
import kotlinx.android.synthetic.main.fragment_main_single_post.singlePostUserName
import java.text.SimpleDateFormat
import java.util.*

class MainSinglePostFragment : Fragment() {
    private val args: MainSinglePostFragmentArgs by navArgs()
    private lateinit var imageAdapter: SinglePostImageAdapter
    private lateinit var videoAdapter: SinglePostVideoAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var commentAdapter: CommentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_single_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        val post = args.passedPost
        Log.d(TAG, "onViewCreated: $post")
        singlePostUserName.text = post.user.name
        singlePostTime.text = SimpleDateFormat("HH:mm dd-MM-yyyy").format(Date(post.time))
        singlePostLocation.text = post.location
        singlePostDescription.text = post.description
        setupImageRecyclerView(post)
        setupVideoRecyclerView(post)
        setupCommentRecyclerView()
        videoAdapter.setOnItemClickListener { video->
            Log.d(TAG, "onViewCreated: $video")
            val action = MainSinglePostFragmentDirections.actionMainSinglePostFragmentToMainVideoPostFragment(video)
            Navigation.findNavController(view).navigate(action)
        }

        postComment.setOnClickListener {
            val comment = comment.text.toString().trim()
            if(comment.isNotEmpty() && comment.isNotBlank()){
                mainViewModel.savePostComment(
                    post, Comment(
                        "",
                        comment,
                        System.currentTimeMillis(),
                        User("","","","")
                    )
                )
            }else{
                Toast.makeText(requireContext(), "Please write something on comment", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.postCommentsResponse.observe(viewLifecycleOwner,{ response ->
            when (response) {
                is MainResponse.Loading -> {
                    Log.d(TAG, "onViewCreated: comments loading")
                }
                is MainResponse.Success -> {
                    commentAdapter.differ.submitList(response.data?.toList())
                    Log.d(TAG, "onViewCreated: comments success ${response.data?.size}")
                }
                is MainResponse.Error -> {
                    Log.d(TAG, "onViewCreated: comments error ${response.message}")
                }
            }
        })

        mainViewModel.addPostCommentResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Loading -> {
                    Log.d(TAG, "onViewCreated: comments loading")
                }
                is MainResponse.Success -> {
                    Toast.makeText(requireContext(), response.data, Toast.LENGTH_SHORT).show()
                }
                is MainResponse.Error -> {
                    Toast.makeText(requireContext(), "Failed to post comment${response.message}", Toast.LENGTH_SHORT).show()
                }
            }

        })

        mainViewModel.loadPostComments(post)
    }

    private fun setupImageRecyclerView(post:Post) {
        imageAdapter = SinglePostImageAdapter(post.images)
        singlePostImageRecycler.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }
    private fun setupVideoRecyclerView(post:Post) {
        videoAdapter = SinglePostVideoAdapter(post.videos)
        singleVideoRecycler.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = videoAdapter
        }
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        commentRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = commentAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.removePostCommentListener()
    }

    companion object {
        private const val TAG = "MyTag:SinglePost"
    }
}