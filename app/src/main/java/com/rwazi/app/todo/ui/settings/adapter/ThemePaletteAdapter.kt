package com.rwazi.app.todo.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rwazi.app.todo.databinding.ItemThemePaletteBinding
import com.rwazi.app.todo.util.ThemePalette

class ThemePaletteAdapter(
    private val onPaletteClick: (ThemePalette) -> Unit
) : ListAdapter<ThemePalette, ThemePaletteAdapter.PaletteViewHolder>(PaletteDiffCallback()) {

    private var selectedThemeResId: Int = 0

    fun setSelectedTheme(resId: Int) {
        selectedThemeResId = resId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val binding = ItemThemePaletteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaletteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaletteViewHolder(private val binding: ItemThemePaletteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(palette: ThemePalette) {
            binding.ivPaletteColor.setBackgroundColor(palette.backgroundColor)
            
            val isSelected = palette.themeResId == selectedThemeResId
            binding.ivSelected.isVisible = isSelected
            
            binding.root.setOnClickListener {
                onPaletteClick(palette)
            }
        }
    }

    class PaletteDiffCallback : DiffUtil.ItemCallback<ThemePalette>() {
        override fun areItemsTheSame(oldItem: ThemePalette, newItem: ThemePalette): Boolean {
            return oldItem.themeResId == newItem.themeResId
        }

        override fun areContentsTheSame(oldItem: ThemePalette, newItem: ThemePalette): Boolean {
            return oldItem == newItem
        }
    }
}
