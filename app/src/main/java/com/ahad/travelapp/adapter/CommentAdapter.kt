package com.ahad.travelapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ahad.travelapp.R
import com.ahad.travelapp.model.Comment
import com.ahad.travelapp.model.Post
import com.ahad.travelapp.util.PostAction
import kotlinx.android.synthetic.main.comment_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val format = SimpleDateFormat("HH:mm dd-MM-yyyy")

    private val differCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comment_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val comment = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(comment.user.imageUrl).into(commentUserImage)
            commentUserName.text = comment.user.name
            commentText.text = comment.comment
            commentTime.text = format.format(Date(comment.time))
        }
    }

    private var onItemClickListener: ((Post,PostAction) -> Unit)? = null

    fun setOnItemClickListener(listener: (Post, PostAction) -> Unit) {
        onItemClickListener = listener
    }
}