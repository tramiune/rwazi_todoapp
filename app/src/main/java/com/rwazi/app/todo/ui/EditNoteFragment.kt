package com.rwazi.app.todo.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.databinding.FragmentEditNoteBinding
import com.rwazi.app.todo.ui.viewmodel.EditNoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditNoteFragment : BaseFragment<FragmentEditNoteBinding, EditNoteViewModel>(
    FragmentEditNoteBinding::inflate
) {
    override val viewModel: EditNoteViewModel by viewModels()
    private var noteId: String? = null

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        noteId = arguments?.getString("noteId")

        if (noteId == null) {
            findNavController().popBackStack()
            return
        }

        setupButtons()
        observeNote()
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            if (title.isNotBlank() || content.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val currentNote = viewModel.getNoteById(noteId!!)
                    currentNote?.let {
                        viewModel.updateNote(it.copy(title = title, content = content))
                        Toast.makeText(
                            requireContext(),
                            "Note updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun observeNote() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getNoteFlow(noteId!!).collectLatest { note ->
                note?.let {
                    binding.etTitle.setText(it.title)
                    binding.etContent.setText(it.content)
                }
            }
        }
    }
}
