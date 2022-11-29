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
import kotlinx.android.synthetic.main.all_post_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class AllPostAdapter: RecyclerView.Adapter<AllPostAdapter.PostViewHolder>() {

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
                R.layout.all_post_layout,
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
            Glide.with(this).load(post.images[0]).into(allPostImage)
            Glide.with(this).load(post.user.imageUrl).into(allPostUserImage)
            allPostUserName.text = post.user.name
            allPostTime.text = format.format(Date(post.time))
            allPostLocation.text = post.location
            allPostDescription.text =
                (if(post.description.length > 75){
                "${post.description.substring(0,75)} more..."
                }else{
                    post.description
                }).toString()

            setOnClickListener {
                onItemClickListener?.let { it(post, PostAction.SHOW_POST) }
            }
        }
    }

    private var onItemClickListener: ((Post,PostAction) -> Unit)? = null

    fun setOnItemClickListener(listener: (Post, PostAction) -> Unit) {
        onItemClickListener = listener
    }
}