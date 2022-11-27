package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahad.travelapp.R
import com.ahad.travelapp.adapter.SinglePostImageAdapter
import com.ahad.travelapp.adapter.SinglePostVideoAdapter
import com.ahad.travelapp.model.Post
import kotlinx.android.synthetic.main.fragment_main_single_post.*
import kotlinx.android.synthetic.main.fragment_main_single_post.singlePostUserName
import java.text.SimpleDateFormat
import java.util.*

class MainSinglePostFragment : Fragment() {
    private val args: MainSinglePostFragmentArgs by navArgs()
    private lateinit var imageAdapter: SinglePostImageAdapter
    private lateinit var videoAdapter: SinglePostVideoAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_single_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val post = args.passedPost
        Log.d(TAG, "onViewCreated: $post")
        singlePostUserName.text = post.user.name
        singlePostTime.text = SimpleDateFormat("HH:mm dd-MM-yyyy").format(Date(post.time))
        singlePostLocation.text = post.location
        singlePostDescription.text = post.description
        setupImageRecyclerView(post)
        setupVideoRecyclerView(post)
        videoAdapter.setOnItemClickListener { video->
            Log.d(TAG, "onViewCreated: $video")
            val action = MainSinglePostFragmentDirections.actionMainSinglePostFragmentToMainVideoPostFragment(video)
            Navigation.findNavController(view).navigate(action)
        }
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

    companion object {
        private const val TAG="MyTag:MainActSinPoF"
    }
}