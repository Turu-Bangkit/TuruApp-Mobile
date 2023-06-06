package com.capstone.turuappmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.DataItem
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.databinding.ItemRowChallengeBinding
import com.capstone.turuappmobile.databinding.ItemSleeptimeRowBinding
import com.capstone.turuappmobile.ui.animation.ShimmerAnimation
import com.capstone.turuappmobile.utils.convertEpochToHour
import com.capstone.turuappmobile.utils.convertEpochToJustDateTime
import com.capstone.turuappmobile.utils.loadImage

class ChallengeSleepAdapter (
    private val challengeList: List<DataItem>,
    private val context: Context,
    private val onClick: (SleepTimeEntity) -> Unit
) : ListAdapter<DataItem, ChallengeSleepAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(var binding: ItemRowChallengeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val challengeEntity = challengeList[position]
        holder.binding.apply {

            namaChallengeTxt.text = challengeEntity.name
            pointsTxt.text = challengeEntity.point.toString()
            imageChallenge.loadImage(challengeEntity.img)

        }
    }

    override fun getItemCount(): Int = challengeList.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItem> =
            object : DiffUtil.ItemCallback<DataItem>() {
                override fun areItemsTheSame(oldUser: DataItem, newUser: DataItem): Boolean {
                    return oldUser.id == newUser.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: DataItem, newUser: DataItem): Boolean {
                    return oldUser == newUser
                }
            }
    }

}