package com.ahad.travelapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahad.travelapp.util.AuthResponse
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val _loginUserResponse = MutableLiveData<AuthResponse<String>>()
    val loginUserResponse: LiveData<AuthResponse<String>>
        get() = _loginUserResponse


    private val _loginResponse = MutableLiveData<AuthResponse<String>>()
    val loginResponse: LiveData<AuthResponse<String>>
        get() = _loginResponse


    private val _regResponse = MutableLiveData<AuthResponse<String>>()
    val regResponse: LiveData<AuthResponse<String>>
        get() = _regResponse


    private val _resetPassResponse = MutableLiveData<AuthResponse<String>>()
    val resetPassResponse: LiveData<AuthResponse<String>>
        get() = _resetPassResponse


    //firebase
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        checkUserLogin()
    }

    private fun checkUserLogin(){
        if(firebaseAuth.currentUser != null){
            _loginUserResponse.value = AuthResponse.Success(firebaseAuth.currentUser?.email.toString())
        }
        else{
            _loginUserResponse.value = AuthResponse.Error("No user signed in")
        }
    }

    fun login(email:String, password:String){
        _loginResponse.value = AuthResponse.Loading()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkUserVerified()
            } else {
                _loginResponse.value = AuthResponse.Error("Error while login",task.exception?.message.toString())
            }
        }.addOnFailureListener { e ->
            _loginResponse.value = AuthResponse.Error("Login failed",e.message.toString())
        }
    }
    private fun checkUserVerified(){
        val currentUser = firebaseAuth.currentUser
        if (currentUser!=null && !currentUser.isEmailVerified) {
            _loginResponse.value = AuthResponse.Error("Login failed","Please verify email")
            firebaseAuth.signOut()
        }else{
            _loginResponse.value = AuthResponse.Success("Login successful")
        }
    }

    fun register(email: String, password: String) {
        _regResponse.value = AuthResponse.Loading()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    sendEmailVerification()
                }
                else{
                    _regResponse.value = AuthResponse.Error("Register failed",task.exception?.message)
                }
            }.addOnFailureListener { e ->
                _regResponse.value = AuthResponse.Error("Register failed",e.message)
            }
    }
    private fun sendEmailVerification() {
        _regResponse.value = AuthResponse.Loading()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser!!.sendEmailVerification()
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    _regResponse.value = AuthResponse.Success("Confirmation email is sent to your account")
                    firebaseAuth.signOut()
                }
                else{
                    _regResponse.value = AuthResponse.Error("Verification email could not sent",task.exception?.message)
                }
            }
            .addOnFailureListener { e ->
                _regResponse.value = AuthResponse.Error("Register failed ",e.message)
            }
    }

    fun resetPassword(email:String){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                _resetPassResponse.value = AuthResponse.Success("Email sent")
            }
            else{
                _resetPassResponse.value = AuthResponse.Error("Error while sending email",task.exception?.message.toString())
            }
        }
    }
}