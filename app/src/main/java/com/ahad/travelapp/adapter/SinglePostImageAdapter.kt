package com.ahad.travelapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahad.travelapp.R
import com.ahad.travelapp.view.MainSinglePostFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_image.view.*

class SinglePostImageAdapter(private val images: List<String>) : RecyclerView.Adapter<SinglePostImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        holder.itemView.apply {
            Glide.with(this).load(images[position]).into(postImage)
        }
    }

    override fun getItemCount() = images.size

    companion object {
        private const val TAG="MyTag:MainActSinPoImAd"
    }

}