package com.capstone.turuappmobile.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.custom.OnProgress
import com.capstone.turuappmobile.databinding.ItemRowChallengeOnprogressBinding

class OnProgressChallengeAdapter(
    private val listOnProgress: List<OnProgress>,
    private val context: Context,
) : RecyclerView.Adapter<OnProgressChallengeAdapter.ListViewHolder>() {

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
        Log.d(
            "DetailChallengeOnProgressActivity",
            "List On Progress: ${data.dateOnProgress}  ${data.bigOnProgress} ${data.statusOnProgress} ${data.hourOnProgress}"
        )
        holder.binding.apply {
            val text: String
            val background : Drawable = when(data.statusOnProgress){
                0 ->{ text = "Completed"
                    ContextCompat.getDrawable(context, R.drawable.circle_shape_green)!!
                }
                1 ->{
                    text = "On Progress"
                    ContextCompat.getDrawable(context, R.drawable.bg_rounded_yellow)!!
                }
                2 ->{
                    text = "Soon"
                    ContextCompat.getDrawable(context, R.drawable.bg_rounded_blue500)!!
                }

                else -> {
                    text = "Soon"
                    ContextCompat.getDrawable(context, R.drawable.bg_rounded_blue500)!!}
            }

            Log.d("DetailChallengeOnProgressActivity", "Text: $text")

            if(position % 2 == 0){
                dateOnProgressEven.visibility = ViewGroup.VISIBLE
                hourOnProgressEven.visibility = ViewGroup.VISIBLE
                statusOnProgressEven.visibility = ViewGroup.VISIBLE
                bigOnProgressEven.visibility = ViewGroup.VISIBLE

                dateOnProgressEven.text = data.dateOnProgress
                hourOnProgressEven.text = data.hourOnProgress

                statusOnProgressEven.text = text
                statusOnProgressEven.background = background
                bigOnProgressEven.text = context.resources.getString(R.string.current_day, data.bigOnProgress)

                dateOnProgressOdd.visibility = ViewGroup.GONE
                hourOnProgressOdd.visibility = ViewGroup.GONE
                statusOnProgressOdd.visibility = ViewGroup.GONE
                bigOnProgressOdd.visibility = ViewGroup.GONE


            }else{
                dateOnProgressOdd.visibility = ViewGroup.VISIBLE
                hourOnProgressOdd.visibility = ViewGroup.VISIBLE
                statusOnProgressOdd.visibility = ViewGroup.VISIBLE
                bigOnProgressOdd.visibility = ViewGroup.VISIBLE

                dateOnProgressOdd.text = data.dateOnProgress
                hourOnProgressOdd.text = data.hourOnProgress

                statusOnProgressOdd.text = text
                statusOnProgressOdd.background = background
                bigOnProgressOdd.text = context.resources.getString(R.string.current_day, data.bigOnProgress)

                dateOnProgressEven.visibility = ViewGroup.GONE
                hourOnProgressEven.visibility = ViewGroup.GONE
                statusOnProgressEven.visibility = ViewGroup.GONE
                bigOnProgressEven.visibility = ViewGroup.GONE

            }
        }


    }

    override fun getItemCount(): Int = listOnProgress.size

}

