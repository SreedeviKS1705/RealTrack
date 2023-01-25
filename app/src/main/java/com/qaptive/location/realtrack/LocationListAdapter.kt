package com.qaptive.location.realtrack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qaptive.advancedrecyclerview.AdvancedRecyclerViewAdapter
import com.qaptive.location.realtrack.databinding.LocationListItemBinding
import javax.security.auth.callback.Callback

class LocationListAdapter (private val callBack: ItemClickCallBack<LocationModel>) :  AdvancedRecyclerViewAdapter<LocationModel,LocationListAdapter.MyViewHolder>(){


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
        holder.binder.location=data?.get(position)

        holder.binder.executePendingBindings()
    }




    class MyViewHolder(val binder: LocationListItemBinding) : RecyclerView.ViewHolder(binder.root){
    }
}