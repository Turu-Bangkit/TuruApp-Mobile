package com.capstone.turuappmobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.databinding.ItemSleeptimeRowBinding
import com.capstone.turuappmobile.utils.convertEpochToDateTime

class HistorySleepAdapter (private val sleepTime : List<SleepTimeEntity>,private val context: Context, private val onClick :(SleepTimeEntity) -> Unit) : RecyclerView.Adapter<HistorySleepAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ItemSleeptimeRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemSleeptimeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val sleepTime = sleepTime[position]
        holder.binding.apply {
            startTime.text = context.resources.getString(R.string.start_time,convertEpochToDateTime(sleepTime.startTime))
            endTime.text = context.resources.getString(R.string.end_time, sleepTime.endTime?.let { convertEpochToDateTime(it) } ?: "-")
            btnGoToDetailHistorySleep.setOnClickListener {
                onClick(sleepTime)
            }
        }
    }

    override fun getItemCount(): Int = sleepTime.size

}