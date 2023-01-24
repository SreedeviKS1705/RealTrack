package com.qaptive.location.realtrack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qaptive.location.realtrack.databinding.LocationListItemBinding

class LocationListAdapter :  RecyclerView.Adapter<LocationListAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: LocationListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.location_list_item,
                parent,
                false)
        return LocationListAdapter.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    class MyViewHolder(val binder: LocationListItemBinding) : RecyclerView.ViewHolder(binder.root){
    }
}