package com.ahad.travelapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ahad.travelapp.R
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.util.PostAction
import kotlinx.android.synthetic.main.user_post_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class UserPostAdapter: RecyclerView.Adapter<UserPostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val format = SimpleDateFormat("HH:mm dd-MM-yyyy")

    private val differCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_post_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(post.images[0]).into(userPostImage)
            Glide.with(this).load(post.user.imageUrl).into(userPostUserImage)
            userPostUserName.text = post.user.name
            userPostTime.text = format.format(Date(post.time))
            userPostLocation.text = post.location
            userPostDescription.text =
                (if(post.description.length > 75){
                "${post.description.substring(0,75)} more..."
                }else{
                    post.description
                }).toString()

            setOnClickListener {
                onItemClickListener?.let { it(post, PostAction.SHOW_POST) }
            }
            userPostDelete.setOnClickListener {
                onItemClickListener?.let { it(post, PostAction.DELETE_POST) }
            }
        }
    }

    private var onItemClickListener: ((Post,PostAction) -> Unit)? = null

    fun setOnItemClickListener(listener: (Post, PostAction) -> Unit) {
        onItemClickListener = listener
    }
}