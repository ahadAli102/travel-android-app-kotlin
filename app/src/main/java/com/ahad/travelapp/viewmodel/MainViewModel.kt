package com.ahad.travelapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.Constant
import com.ahad.travelapp.util.MainResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userProfileListenerRegistration: ListenerRegistration
    private var imageReference = FirebaseStorage.getInstance().getReference("user_profile_image")

    private val _userProfileResponse = MutableLiveData<MainResponse<User>>()
    val userProfileResponse: LiveData<MainResponse<User>>
        get() = _userProfileResponse

    private val _userImageInsertResponse = MutableLiveData<MainResponse<String>>()
    val userImageInsertResponse: LiveData<MainResponse<String>>
        get() = _userImageInsertResponse

    private val _userNameInsertResponse = MutableLiveData<MainResponse<String>>()
    val userNameInsertResponse: LiveData<MainResponse<String>>
        get() = _userNameInsertResponse

    private val _signOutResponse = MutableLiveData<MainResponse<String>>()
    val signOutResponse: LiveData<MainResponse<String>>
        get() = _signOutResponse

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() = viewModelScope.launch {
        _userProfileResponse.postValue(MainResponse.Loading())

        val uId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d(TAG, "loadUserInfo:  $uId")
        userProfileListenerRegistration = firestore.collection(USER_PROFILE_KEY).document(uId!!)
            .addSnapshotListener(MetadataChanges.INCLUDE
            ) { value, error ->
                Log.d(TAG, "loadUserInfo: response")
                if(error != null){
                    Log.d(TAG, "loadUserInfo: error ${error.localizedMessage}")
                    _userProfileResponse.postValue(
                        MainResponse.Error(error.localizedMessage,User(uId, "No user name", FirebaseAuth.getInstance().currentUser?.email!!, null))
                    )
                }
                if(value!=null){
                    Log.d(TAG, "loadUserInfo: value $value")
                    val name = value[Constant.User.NAME]?:"No name"
                    val email = value[Constant.User.EMAIL]?:FirebaseAuth.getInstance().currentUser?.email!!
                    var imageUrl:String? = null
                    value[Constant.User.IMAGE_URL]?.let {
                        imageUrl = it.toString()
                    }
                    _userProfileResponse.postValue(
                        MainResponse.Success(
                            User(uId, name.toString(), email.toString(), imageUrl
                            )
                        )
                    )
                }
            }
    }

    fun saveUserImage(image: Uri, fileName:String, user: User) = viewModelScope.launch{
        _userImageInsertResponse.postValue(MainResponse.Loading())
        storeImageToStorage(image,fileName, user)
    }

    private fun storeImageToStorage(image: Uri,fileName: String, user: User){
        imageReference.child(user.uId).child(fileName).putFile(image)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    imageReference.child(user.uId).child(fileName).downloadUrl.addOnCompleteListener { url->
                        saveUserImageUrl(url.result.toString(), user)
                    }
                }
                else{
                    _userImageInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage.toString()))
                }
            }
    }
    private fun saveUserImageUrl(url: String, user: User) {
        val userMap = mutableMapOf<String,String>()
        userMap[Constant.User.NAME] = user.name
        userMap[Constant.User.EMAIL] = user.email
        userMap[Constant.User.IMAGE_URL] = url
        firestore.collection(USER_PROFILE_KEY).document(user.uId).set(userMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _userImageInsertResponse.postValue(MainResponse.Success("New image inserted"))
                }
                else{
                    _userImageInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage.toString()))
                }
            }
    }

    fun saveUserName(name:String,user: User) = viewModelScope.launch{
        _userNameInsertResponse.postValue(MainResponse.Loading())
        val userMap = mutableMapOf<String,String?>()
        userMap[Constant.User.NAME] = name
        userMap[Constant.User.EMAIL] = user.email
        userMap[Constant.User.IMAGE_URL] = null
        user.imageUrl?.let {
            userMap[Constant.User.IMAGE_URL] = it.toString()
        }
        firestore.collection(USER_PROFILE_KEY).document(user.uId).set(userMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _userNameInsertResponse.postValue(MainResponse.Success("User name changed"))
                }
                else{
                    _userNameInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage.toString()))
                }
            }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    companion object{
        private const val USER_PROFILE_KEY = "users"
        private const val TAG = "MyTag:MainViewModel"
    }
}