package com.ahad.travelapp.view

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ahad.travelapp.R
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main_add_post.*

class MainAddPostFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private var images:MutableList<Uri>? = null
    private var videos:MutableList<Uri>? = null

    private val multipleImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.clipData?.let { clipData: ClipData ->
                val size = clipData.itemCount
                addPostPickImageQuantity.text = "$size image selected"
                if(size != 0){
                    images = mutableListOf()
                    for (i in 0.until(clipData.itemCount)) {
                        images!!.add(clipData.getItemAt(i).uri)
                    }
                }
            }
        }
    }
    private val multipleVideoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.clipData?.let { clipData: ClipData ->
                val size = clipData.itemCount
                addPostPickVideoQuantity.text = "$size video selected"
                if(size != 0){
                    videos = mutableListOf()
                    for (i in 0.until(clipData.itemCount)) {
                        videos!!.add(clipData.getItemAt(i).uri)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        addPostPickImage.setOnClickListener {
            pickMultipleImages()
        }
        addPostPickVideo.setOnClickListener {
            pickMultipleVideos()
        }
        addPostAddButton.setOnClickListener {
            val location = addPostLocation.text.toString().trim()
            val description = addPostDescription.text.toString().trim()
            if(location.isEmpty() || location.isBlank()){
                Toast.makeText(activity, "Please enter location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(description.isEmpty() || description.isBlank()){
                Toast.makeText(activity, "Please enter description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(images == null){
                Toast.makeText(activity, "Please select images", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(videos == null){
                Toast.makeText(activity, "Please select videos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mainViewModel.savePost(
                Post("",location,description, emptyList(), emptyList(),System.currentTimeMillis(),
                    User("","","","")
                )
                ,images!!, videos!!
            )
        }

        mainViewModel.addPostResponse.observe(viewLifecycleOwner, {response->
            when(response){
                is MainResponse.Success -> {
                    addPostProgress.text = response.data
                    makeAllDefault()
                }
                is MainResponse.Progress -> {
                    addPostProgress.text = response.data
                }
                is MainResponse.Error -> {
                    addPostProgress.text = response.data
                    makeAllDefault()
                }
            }
        })
    }
    private fun makeAllDefault(){
        addPostPickImageQuantity.text = "no image is selected"
        addPostPickVideoQuantity.text = "no video is selected"
        images?.clear()
        videos?.clear()
    }

    private fun pickMultipleImages(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).also{
            it.type="image/*"
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        multipleImageResultLauncher.launch(intent)
    }

    private fun pickMultipleVideos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).also{
            it.type="video/*"
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        multipleVideoResultLauncher.launch(intent)
    }

    companion object {

    }
}