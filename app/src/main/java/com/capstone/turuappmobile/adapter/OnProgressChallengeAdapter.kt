package com.capstone.turuappmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.custom.OnProgress
import com.capstone.turuappmobile.databinding.ItemRowChallengeOnprogressBinding

class OnProgressChallengeAdapter(
    private val listOnProgress: List<OnProgress>,
) : ListAdapter<OnProgress, OnProgressChallengeAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(var binding: ItemRowChallengeOnprogressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowChallengeOnprogressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listOnProgress[position]

        holder.binding.apply {
            var text = ""
            val color = when(data.statusOnProgress){
                0 ->{ text = "Completed"
                    R.color.green_200
                }
                1 ->{
                    text = "On Progress"
                    R.color.yellow_100
                }
                2 ->{
                    text = "Soon"
                    R.color.blue_600
                }
                else ->{
                    text = "Expired"
                    R.color.red_200
                }
            }
            if(position % 2 == 0){
                dateOnProgressEven.visibility = ViewGroup.VISIBLE
                hourOnProgressEven.visibility = ViewGroup.VISIBLE
                statusOnProgressEven.visibility = ViewGroup.VISIBLE
                bigOnProgressEven.visibility = ViewGroup.VISIBLE

                dateOnProgressEven.text = data.dateOnProgress
                hourOnProgressEven.text = data.hourOnProgress

                statusOnProgressEven.text = text
                statusOnProgressEven.setBackgroundColor(color)
                bigOnProgressEven.text = data.bigOnProgress.toString()
            }else{
                dateOnProgressOdd.visibility = ViewGroup.VISIBLE
                hourOnProgressOdd.visibility = ViewGroup.VISIBLE
                statusOnProgressOdd.visibility = ViewGroup.VISIBLE
                bigOnProgressOdd.visibility = ViewGroup.VISIBLE

                dateOnProgressOdd.text = data.dateOnProgress
                hourOnProgressOdd.text = data.hourOnProgress

                statusOnProgressEven.text = text
                statusOnProgressEven.setBackgroundColor(color)
                bigOnProgressOdd.text = data.bigOnProgress.toString()
            }
        }


    }

    override fun getItemCount(): Int = listOnProgress.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<OnProgress> =
            object : DiffUtil.ItemCallback<OnProgress>() {
                override fun areItemsTheSame(
                    oldUser: OnProgress,
                    newUser: OnProgress
                ): Boolean {
                    return oldUser.bigOnProgress == newUser.bigOnProgress
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldUser: OnProgress,
                    newUser: OnProgress
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }
}

