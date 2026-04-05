package com.rwazi.app.todo.ui.edit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.databinding.FragmentEditNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteFragment : BaseFragment<FragmentEditNoteBinding, EditNoteViewModel>(
    FragmentEditNoteBinding::inflate
) {

    override val shouldObserveViewModelState: Boolean = false

    override val classTypeOfViewModel: Class<EditNoteViewModel>
        get() = EditNoteViewModel::class.java

    private val args: EditNoteFragmentArgs by navArgs()
    private var isNoteLoaded = false

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        val noteId = args.noteId
        if (noteId.isBlank()) {
            findNavController().popBackStack()
            return
        }
    }

    override fun setupClick() {
        setupButtons()
    }

    override fun observer() {
        observeNote()
        observeEffects()
    }

    private fun setupButtons() {
        binding.topBar.setOnBackClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.click {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()
            viewModel.updateNote(args.noteId, title, content)
        }
    }

    private fun observeNote() {
        viewModel.getNoteFlow(args.noteId).collectInStarted(this) { note ->
            if (isNoteLoaded) return@collectInStarted
            note?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
                isNoteLoaded = true
            }
        }
    }

    private fun observeEffects() {
        viewModel.effect.collectInStarted(this) { effect ->
            when (effect) {
                is EditNoteViewModel.EditEffect.UpdateSuccess -> {
                    showToast(
                        getString(com.rwazi.app.todo.R.string.note_updated),
                        Toast.LENGTH_SHORT
                    )
                    findNavController().popBackStack()
                }
            }
        }
    }

}