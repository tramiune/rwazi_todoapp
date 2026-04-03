package com.rwazi.app.todo.ui.home.adapter

import android.content.res.Configuration
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.domain.model.Note
import com.rwazi.app.todo.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteViewHolder(
    private val binding: ItemNoteBinding,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteDelete: (Note) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    fun bind(note: Note) {
        binding.apply {
            tvTitle.text = note.title
            tvContent.text = note.content
            tvDate.text = dateFormat.format(Date(note.createdAt))

            val isNightMode =
                (root.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            if (isNightMode) {
                root.setCardBackgroundColor("#333333".toColorInt())
            } else {
                root.setCardBackgroundColor(note.backgroundColor)
            }

            root.click { onNoteClick(note) }
            btnDelete.click { onNoteDelete(note) }
        }
    }
}
