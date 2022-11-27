package com.ahad.travelapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahad.travelapp.model.Comment
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.model.User
import com.ahad.travelapp.util.Constant
import com.ahad.travelapp.util.MainResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userProfileListenerRegistration: ListenerRegistration
    private lateinit var userPostListenerRegistration: ListenerRegistration
    private lateinit var postCommentListenerRegistration: ListenerRegistration
    private var imageReference = FirebaseStorage.getInstance().getReference("user_profile_image")
    private var postReference =
        FirebaseStorage.getInstance().getReference("post_resource").child(FirebaseAuth.getInstance().currentUser!!.uid)


    private lateinit var user: User

    private val _userProfileResponse = MutableLiveData<MainResponse<User>>()
    val userProfileResponse: LiveData<MainResponse<User>>
        get() = _userProfileResponse

    private val _userImageInsertResponse = MutableLiveData<MainResponse<String>>()
    val userImageInsertResponse: LiveData<MainResponse<String>>
        get() = _userImageInsertResponse

    private val _userNameInsertResponse = MutableLiveData<MainResponse<String>>()
    val userNameInsertResponse: LiveData<MainResponse<String>>
        get() = _userNameInsertResponse

    private val _addPostResponse = MutableLiveData<MainResponse<String>>()
    val addPostResponse: LiveData<MainResponse<String>>
        get() = _addPostResponse

    private val _userPostResponse = MutableLiveData<MainResponse<MutableList<Post>>>()
    val userPostResponse: LiveData<MainResponse<MutableList<Post>>>
        get() = _userPostResponse

    private val _userPostDeleteResponse = MutableLiveData<MainResponse<String>>()
    val userPostDeleteResponse: LiveData<MainResponse<String>>
        get() = _userPostDeleteResponse

    private val _addPostCommentResponse = MutableLiveData<MainResponse<String>>()
    val addPostCommentResponse: LiveData<MainResponse<String>>
        get() = _addPostCommentResponse

    private val _postCommentsResponse = MutableLiveData<MainResponse<List<Comment>>>()
    val postCommentsResponse: LiveData<MainResponse<List<Comment>>>
        get() = _postCommentsResponse

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
                    user = User(uId, "No user name", FirebaseAuth.getInstance().currentUser?.email!!, null)
                    _userProfileResponse.postValue(
                        MainResponse.Error(error.localizedMessage!!,user)
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
                    user = User(uId, name.toString(), email.toString(), imageUrl)
                    _userProfileResponse.postValue(
                        MainResponse.Success(user)
                    )
                }
                loadUserPost()
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
                    _userImageInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage!!))
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
                    _userImageInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage!!))
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
            userMap[Constant.User.IMAGE_URL] = it
        }
        firestore.collection(USER_PROFILE_KEY).document(user.uId).set(userMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _userNameInsertResponse.postValue(MainResponse.Success("User name changed"))
                }
                else{
                    _userNameInsertResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage!!))
                }
            }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun savePost(post: Post, images:MutableList<Uri>, videos:MutableList<Uri>) = viewModelScope.launch {
        _addPostResponse.postValue(MainResponse.Progress("Adding post"))
        var pos = 1
        val imageUrls = mutableListOf<String>()
        val videoUrls = mutableListOf<String>()
        for (image in images) {
            imageUrls.add(savePostResource(pos,image,"images",post.time))
            _addPostResponse.postValue(MainResponse.Progress("${pos++} image is uploaded"))
        }
        pos = 1
        for (video in videos) {
            videoUrls.add(savePostResource(pos,video,"videos",post.time))
            _addPostResponse.postValue(MainResponse.Progress("${pos++} video is uploaded"))
        }
        val postMap = mutableMapOf<String,Any>()
        postMap[Constant.Post.LOCATION] = post.location
        postMap[Constant.Post.DESCRIPTION] = post.description
        postMap[Constant.Post.TIME] = post.time
        postMap[Constant.Post.IMAGES] = imageUrls
        postMap[Constant.Post.VIDEOS] = videoUrls
        postMap[Constant.Post.USER_ID] = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection(POSTS_KEY).document().set(postMap).addOnCompleteListener { task->
            if(task.isSuccessful){
                _addPostResponse.postValue(MainResponse.Success("Post is inserted"))
            }else{
                _addPostResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage!!))
            }
        }
    }
    private suspend fun savePostResource(pos:Int, resource:Uri, type:String, time:Long):String {
        return postReference.child("${time}/${type}/${pos}")
            .putFile(resource)
            .await() // await() instead of snapshot
            .storage
            .downloadUrl
            .await() // await the url
            .toString()
    }
    private fun loadUserPost() {
        _userPostResponse.postValue(MainResponse.Loading())
        userPostListenerRegistration = firestore.collection(POSTS_KEY)
            .whereEqualTo(Constant.Post.USER_ID , user.uId)
            .orderBy(Constant.Post.TIME,Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot: QuerySnapshot?,
                                                            firebaseFirestoreException: FirebaseFirestoreException? ->
                firebaseFirestoreException?.let {
                    _userPostResponse.postValue(MainResponse.Error(it.localizedMessage!!))
                }
                querySnapshot?.let {
                    val userPosts = mutableListOf<Post>()
                    for(document in it.documents){
                        userPosts.add(
                            Post(
                                document.id,
                                document[Constant.Post.LOCATION] as String,
                                document[Constant.Post.DESCRIPTION] as String,
                                document[Constant.Post.IMAGES] as List<String>,
                                document[Constant.Post.VIDEOS] as List<String>,
                                document[Constant.Post.TIME] as Long,
                                user
                            )
                        )
                    }
                    _userPostResponse.postValue(MainResponse.Success(userPosts))
                }

            }
    }

    fun deletePost(post: Post) = viewModelScope.launch {
        _userPostDeleteResponse.postValue(MainResponse.Loading())
        deletePostContent(post)
        firestore.collection(POSTS_KEY).document(post.id).delete().addOnCompleteListener {
            if(it.isSuccessful){
                _userPostDeleteResponse.postValue(MainResponse.Success("Post deleted"))
            }else{
                _userPostDeleteResponse.postValue(it.exception?.let { it1 ->
                    MainResponse.Error(
                        it1.localizedMessage!!
                    )
                })
            }
        }
    }

    private suspend fun deletePostContent(post: Post) {
        for(image in post.images){
            FirebaseStorage.getInstance().getReferenceFromUrl(image).delete().await()
        }
        for(videos in post.videos){
            FirebaseStorage.getInstance().getReferenceFromUrl(videos).delete().await()
        }
    }

    fun savePostComment(post: Post, comment: Comment) = viewModelScope.launch{
        _addPostCommentResponse.postValue(MainResponse.Loading())

        val commentMap = mutableMapOf<String,Any?>()
        commentMap[Constant.Comment.COMMENT] = comment.comment
        commentMap[Constant.Comment.TIME] = comment.time
        commentMap[Constant.Comment.USER_IMAGE] = user.imageUrl
        commentMap[Constant.Comment.USER_NAME] = user.name

        firestore.collection(ALL_COMMENTS_KEY).document(post.id).collection(COMMENTS_KEY).document(user.uId)
            .set(commentMap, SetOptions.merge()).addOnCompleteListener { task->
                if(task.isSuccessful){
                    _addPostCommentResponse.postValue(MainResponse.Success("Comment has inserted"))
                }
                else if(task.exception!=null){
                    _addPostCommentResponse.postValue(MainResponse.Error(task.exception!!.localizedMessage!!))
                }
            }
    }

    fun loadPostComments(post: Post) = viewModelScope.launch{
        _postCommentsResponse.postValue(MainResponse.Loading())
        postCommentListenerRegistration = firestore.collection(ALL_COMMENTS_KEY).document(post.id).collection(COMMENTS_KEY)
            .orderBy(Constant.Comment.TIME,Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) {
                    querySnapshot: QuerySnapshot?,
                    firebaseFirestoreException: FirebaseFirestoreException? ->

                firebaseFirestoreException?.let {
                    _postCommentsResponse.postValue(MainResponse.Error(it.localizedMessage!!))
                }

                querySnapshot?.let {
                    val comments = mutableListOf<Comment>()
                    for(document in it.documents){
                        comments.add(
                            Comment(
                                document.id,
                                document[Constant.Comment.COMMENT] as String,
                                document[Constant.Comment.TIME] as Long,
                                User("",
                                    (document[Constant.Comment.USER_NAME]?:"No name") as String,"",
                                    (document[Constant.Comment.USER_IMAGE]?:"") as String)
                            )
                        )
                    }
                    _postCommentsResponse.postValue(MainResponse.Success(comments))
                }
            }
    }

    fun removePostCommentListener(){
        postCommentListenerRegistration.remove()
    }

    companion object{
        private const val USER_PROFILE_KEY = "users"
        private const val POSTS_KEY = "posts"
        private const val ALL_COMMENTS_KEY = "all_comments"
        private const val COMMENTS_KEY = "comments"
        private const val TAG = "MyTag:MainViewModel"
    }
}