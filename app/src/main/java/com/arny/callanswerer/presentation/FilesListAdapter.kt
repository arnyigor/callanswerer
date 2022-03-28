package com.arny.callanswerer.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arny.callanswerer.databinding.IFileItemBinding

class FilesListAdapter(
    private val onClick: (item: String) -> Unit = {}
) : ListAdapter<String, FilesListAdapter.FilesListViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }) {

    inner class FilesListViewHolder(private val binding: IFileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.tvTitle.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesListViewHolder =
        FilesListViewHolder(
            IFileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FilesListViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }
}