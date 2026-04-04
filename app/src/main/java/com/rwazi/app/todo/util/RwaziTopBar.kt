package com.rwazi.app.todo.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rwazi.app.todo.R
import com.rwazi.app.todo.databinding.ViewTopBarBinding
import androidx.core.content.withStyledAttributes

class RwaziTopBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewTopBarBinding =
        ViewTopBarBinding.inflate(LayoutInflater.from(context), this)

    var title: String?
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }

    init {

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.RwaziTopBar) {
                val titleText = getString(R.styleable.RwaziTopBar_topBarTitle)
                title = titleText
            }
        }
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        binding.btnBack.setOnClickListener { listener() }
    }
}
