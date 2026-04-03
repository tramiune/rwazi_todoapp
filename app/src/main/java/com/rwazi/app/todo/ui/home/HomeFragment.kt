package com.rwazi.app.todo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.textfield.TextInputEditText
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import com.rwazi.app.todo.databinding.FragmentHomeBinding
import com.rwazi.app.todo.ui.home.adapter.NoteAdapter
import com.rwazi.app.todo.util.ColorUtils
import com.rwazi.app.todo.util.SortOrder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {
    override val classTypeOfViewModel: Class<HomeViewModel>
        get() = HomeViewModel::class.java
    private lateinit var adapter: NoteAdapter

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun listener() {
        setupSearchView()
        setupFab()
        setupSettings()
        setupSort()
    }

    override fun observer() {
        observeNotes()
    }

    private fun setupSort() {
        binding.btnSort.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.menu.add(0, 0, 0, getString(R.string.sort_newest))
            popup.menu.add(0, 1, 1, getString(R.string.sort_oldest))

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    0 -> viewModel.setSortOrder(SortOrder.NEWEST_FIRST)
                    1 -> viewModel.setSortOrder(SortOrder.OLDEST_FIRST)
                }
                true
            }
            popup.show()
        }
    }

    private fun setupSettings() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SettingsFragment)
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
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
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

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val isListEmpty =
                    loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.llEmptyState.visibility = if (isListEmpty) View.VISIBLE else View.GONE
                binding.rvNotes.visibility = if (isListEmpty) View.GONE else View.VISIBLE
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_note, null)
        val etTitle =
            dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etContent =
            dialogView.findViewById<TextInputEditText>(R.id.etContent)

        AlertDialog.Builder(requireContext())
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