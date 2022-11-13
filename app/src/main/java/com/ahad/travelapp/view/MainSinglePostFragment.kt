package com.ahad.travelapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ahad.travelapp.R
import kotlinx.android.synthetic.main.fragment_main_single_post.*

class MainSinglePostFragment : Fragment() {
    private val args: MainSinglePostFragmentArgs by navArgs()
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
        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("passed_post", post)
            findNavController().navigate(R.id.action_mainSinglePostFragment_to_mainVideoPostFragment,bundle)
        }
    }

    companion object {
        private const val TAG="MyTag:MainActSinPoF"
    }
}