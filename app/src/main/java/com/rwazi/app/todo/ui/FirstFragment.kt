package com.rwazi.app.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.databinding.FragmentFirstBinding
import com.rwazi.app.todo.util.ColorUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FirstFragment : com.rwazi.app.todo.base.BaseFragment<FragmentFirstBinding, NoteViewModel>(
    FragmentFirstBinding::inflate
) {
    override val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    @javax.inject.Inject
    lateinit var authRepository: com.rwazi.app.todo.data.repository.AuthRepository

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        binding.root.setBackgroundColor(ColorUtils.getRandomAppBackgroundColor())

        setupRecyclerView()
        setupSearchView()
        setupFab()
        setupLogout()
        setupSort()
        observeNotes()
    }

    private fun setupSort() {
        binding.btnSort.setOnClickListener {
            val popup = androidx.appcompat.widget.PopupMenu(requireContext(), it)
            popup.menu.add(0, 0, 0, getString(R.string.sort_newest))
            popup.menu.add(0, 1, 1, getString(R.string.sort_oldest))
            
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    0 -> viewModel.setSortOrder(com.rwazi.app.todo.util.SortOrder.NEWEST_FIRST)
                    1 -> viewModel.setSortOrder(com.rwazi.app.todo.util.SortOrder.OLDEST_FIRST)
                }
                true
            }
            popup.show()
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.signOut()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            onNoteClick = { note ->
                val bundle = Bundle().apply {
                    putString("noteId", note.id)
                }
                findNavController().navigate(R.id.action_FirstFragment_to_EditNoteFragment, bundle)
            },
            onNoteDelete = { note ->
                viewModel.deleteNote(note.id)
            }
        )
        binding.rvNotes.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchQueryChanged(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun observeNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notes.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_note, null)
        val etTitle = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etContent)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_new_note))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val title = etTitle.text?.toString() ?: ""
                val content = etContent.text?.toString() ?: ""
                if (title.isNotBlank() || content.isNotBlank()) {
                    viewModel.addNote(
                        title = title,
                        content = content,
                        backgroundColor = ColorUtils.getRandomNoteColor()
                    )
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}