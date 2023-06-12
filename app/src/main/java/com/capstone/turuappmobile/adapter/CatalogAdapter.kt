package com.capstone.turuappmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.DataCatalog
import com.capstone.turuappmobile.databinding.ItemRowCatalogBinding
import com.capstone.turuappmobile.utils.loadImage

class CatalogAdapter(
    private val catalogList: List<DataCatalog>,
    private val context: Context,
    private val onClick: (DataCatalog) -> Unit
) : ListAdapter<DataCatalog, CatalogAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(var binding: ItemRowCatalogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowCatalogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val catalogEntity = catalogList[position]
        holder.binding.apply {

            imageCatalog.loadImage(catalogEntity.img)
            namaCatalogTxt.text = catalogEntity.name
            pointsTxt.text =  context.resources.getString(R.string.points, catalogEntity.point.toString())

        }

        holder.itemView.setOnClickListener {
            onClick(catalogEntity)
        }

    }

    override fun getItemCount(): Int = catalogList.size


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataCatalog> =
            object : DiffUtil.ItemCallback<DataCatalog>() {
                override fun areItemsTheSame(oldUser: DataCatalog, newUser: DataCatalog): Boolean {
                    return oldUser.id == newUser.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: DataCatalog, newUser: DataCatalog): Boolean {
                    return oldUser == newUser
                }
            }
    }

}