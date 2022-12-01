package com.ahad.travelapp.view

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ahad.travelapp.R
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.MainResponse
import com.ahad.travelapp.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main_profile.*

class MainProfileFragment : Fragment() {
    lateinit var mainViewModel: MainViewModel
    lateinit var user: User
    private val singleImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { image->
                getFileName(image)?.let { fileName ->
                    mainViewModel.saveUserImage(image, fileName,user)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        mainViewModel.userProfileResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Success -> {
                    user = response.data!!
                    userNameId.text = user.name
                    userEmailId.text = user.email
                    user.imageUrl?.let {
                        if (!user.imageUrl.equals("null")) {
                            Glide.with(activity as MainActivity).load(user.imageUrl).into(profileImageId)
                        }
                    }
                    homeProgressId.visibility = View.GONE
                    Log.d(TAG, "onViewCreated: userProfileResponse success $user")
                }
                is MainResponse.Loading -> {
                    homeProgressId.visibility = View.VISIBLE
                    Log.d(TAG, "onViewCreated: userProfileResponse loading")
                }
                is MainResponse.Error -> {
                    homeProgressId.visibility = View.GONE
                    user = response.data!!
                    userNameId.text = user.name
                    userEmailId.text = user.email
                    Log.d(TAG, "onViewCreated: userProfileResponse error ${response.message} $user")
                }
            }
        })
        mainViewModel.userImageInsertResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Success -> {
                    homeProgressId.visibility = View.GONE
                }
                is MainResponse.Loading -> {
                    homeProgressId.visibility = View.VISIBLE
                }
                is MainResponse.Error -> {
                    homeProgressId.visibility = View.GONE
                    Toast.makeText(activity, "Failed: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.postStatResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is MainResponse.Success -> {
                    totalPosts.text = "Posts:${response.data?.postsAmount}"
                    singlePostTotalRating.text = "Ratings:${response.data?.postRatings}"
                    singlePostAvgRating.text = "Rating:${response.data?.postRating}"
                }
            }
        })
        profileImageId.setOnClickListener {
            Log.d(TAG, "onViewCreated: profile click")
            Snackbar.make(view, "Do you want to change profile image?", Snackbar.LENGTH_SHORT)
                .setAction("YES") {
                    chooseImage()
                }.show()
        }
        editIconId.setOnClickListener {
            EditUserNameFragment(user,mainViewModel).show(childFragmentManager,"")
        }
        signOutButtonId.setOnClickListener {
            mainViewModel.signOut()
            startActivity(Intent((activity as MainActivity),AuthActivity::class.java))
            (activity as MainActivity).finish()
        }
    }
    private fun chooseImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).also{
            it.type="image/*"
        }
        singleImageResultLauncher.launch(intent)
    }
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    companion object {
        private const val TAG = "MyTag:ProfileFragment"
    }
}