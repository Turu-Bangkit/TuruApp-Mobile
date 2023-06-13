package com.capstone.turuappmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.databinding.ItemSleeptimeRowBinding
import com.capstone.turuappmobile.utils.convertEpochToHour
import com.capstone.turuappmobile.utils.convertEpochToJustDateTime

class HistorySleepAdapter(
    private val sleepTime: List<SleepTimeEntity>,
    private val context: Context,
    private val onClick: (SleepTimeEntity) -> Unit
) : ListAdapter<SleepTimeEntity,HistorySleepAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(var binding: ItemSleeptimeRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemSleeptimeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val sleepTime = sleepTime[position]
        val startTime = convertEpochToJustDateTime(sleepTime.startTime)
        val endTime = sleepTime.endTime?.let { convertEpochToJustDateTime(it) }
        val startTimeHour = convertEpochToHour(sleepTime.startTime)
        val endTimeHour = sleepTime.endTime?.let { convertEpochToHour(it) }
        holder.binding.apply {

            startendTime.text = if (startTime == endTime) {
                startTime
            } else {
                context.resources.getString(R.string.range_date, startTime, endTime)
            }


            startendHour.text =
                context.resources.getString(R.string.range_hour, startTimeHour, endTimeHour)
            btnGoToDetailHistorySleep.setOnClickListener {
                onClick(sleepTime)
            }
        }
    }

    override fun getItemCount(): Int = sleepTime.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<SleepTimeEntity> =
            object : DiffUtil.ItemCallback<SleepTimeEntity>() {
                override fun areItemsTheSame(oldUser: SleepTimeEntity, newUser: SleepTimeEntity): Boolean {
                    return oldUser.startTime == newUser.startTime
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: SleepTimeEntity, newUser: SleepTimeEntity): Boolean {
                    return oldUser == newUser
                }
            }
    }

}