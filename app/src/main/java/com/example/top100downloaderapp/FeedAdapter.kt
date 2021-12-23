package com.example.top100downloaderapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.top100downloaderapp.databinding.RvFeedItemBinding
import android.widget.TextView
import androidx.core.view.allViews
import android.opengl.Visibility
import android.view.View


class FeedAdapter (private val items:ArrayList<FeedEntry>): RecyclerView.Adapter<FeedAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: RvFeedItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RvFeedItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val title =items[position].name
        holder.binding.apply {
            tvFeed.text = title
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }




}