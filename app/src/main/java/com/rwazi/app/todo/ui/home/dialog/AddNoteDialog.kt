package com.rwazi.app.todo.ui.home.dialog

import androidx.fragment.app.viewModels
import com.rwazi.app.todo.base.BaseDialogFragmentAR
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.databinding.DialogAddNoteBinding
import com.rwazi.app.todo.ui.home.HomeViewModel
import com.rwazi.app.todo.util.ColorUtils

class AddNoteDialog : BaseDialogFragmentAR<DialogAddNoteBinding>() {

    private val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initDialog() {
        setupClick()
    }

    private fun setupClick() {
        binding.btnCancel.click {
            dismiss()
        }

        binding.btnAdd.click {
            val title = binding.etTitle.text?.toString().orEmpty()
            val content = binding.etContent.text?.toString().orEmpty()
            if (title.isNotBlank() || content.isNotBlank()) {
                viewModel.addNote(
                    title = title,
                    content = content,
                    backgroundColor = ColorUtils.getRandomNoteColor()
                )
                dismiss()
            }
        }
    }
}
