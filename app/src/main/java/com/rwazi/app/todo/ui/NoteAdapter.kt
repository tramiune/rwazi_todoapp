package com.rwazi.app.todo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onNoteClick: (NoteEntity) -> Unit,
    private val onNoteDelete: (NoteEntity) -> Unit
) : PagingDataAdapter<NoteEntity, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        if (note != null) {
            holder.bind(note)
        }
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(note: NoteEntity) {
            binding.apply {
                tvTitle.text = note.title
                tvContent.text = note.content
                tvDate.text = dateFormat.format(Date(note.createdAt))
                
                val isNightMode = (root.context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
                if (isNightMode) {
                    root.setCardBackgroundColor(android.graphics.Color.parseColor("#333333")) // Standard dark sticky note
                } else {
                    root.setCardBackgroundColor(note.backgroundColor)
                }
                
                root.setOnClickListener { onNoteClick(note) }
                btnDelete.setOnClickListener { onNoteDelete(note) }
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteEntity>() {
        override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
