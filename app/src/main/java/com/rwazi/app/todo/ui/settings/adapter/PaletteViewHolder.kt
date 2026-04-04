package com.rwazi.app.todo.ui.settings.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rwazi.app.todo.databinding.ItemThemePaletteBinding
import com.rwazi.app.todo.util.ThemePalette

class PaletteViewHolder(
    private val binding: ItemThemePaletteBinding,
    private val onPaletteClick: (ThemePalette) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(palette: ThemePalette) {
        binding.ivPaletteColor.setBackgroundColor(palette.backgroundColor)
        
        binding.ivSelected.isVisible = palette.isSelected
        
        binding.root.setOnClickListener {
            onPaletteClick(palette)
        }
    }
}
