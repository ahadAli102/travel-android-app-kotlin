package com.ahad.travelapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val map = hashMapOf<String,String>()
        map["name"] = "adi"
        firestore.collection("name").document().set(map)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d("MyTag", "insertData: inserted")
                }
                else{
                    Log.d("MyTag", "insertData: error ${task.exception!!.message!!}")
                }
            }
    }
}