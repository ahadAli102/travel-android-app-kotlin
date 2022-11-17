package com.ahad.travelapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahad.travelapp.R
import kotlinx.android.synthetic.main.post_video.view.*

class SinglePostVideoAdapter(private val videos: List<String>) : RecyclerView.Adapter<SinglePostVideoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_video,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            postVideoText.text = "Video-${position+1}"
            setOnClickListener {
                onItemClickListener?.let { it(videos[position]) }
            }
        }
    }

    override fun getItemCount() = videos.size

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

}