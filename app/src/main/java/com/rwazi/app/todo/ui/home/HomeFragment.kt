package com.rwazi.app.todo.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.base.extension.goneView
import com.rwazi.app.todo.base.extension.visibleView
import com.rwazi.app.todo.databinding.DialogAddNoteBinding
import com.rwazi.app.todo.databinding.FragmentHomeBinding
import com.rwazi.app.todo.ui.home.adapter.NoteAdapter
import com.rwazi.app.todo.util.ColorUtils
import com.rwazi.app.todo.domain.model.SortOrder

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {
    override val classTypeOfViewModel: Class<HomeViewModel>
        get() = HomeViewModel::class.java
    private val adapter = NoteAdapter(
        onNoteClick = { note ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note.id)
            findNavController().navigate(action)
        },
        onNoteDelete = { note ->
            viewModel.deleteNote(note.id)
        }
    )

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        binding.root.setBackgroundColor(ColorUtils.getRandomSoftColor())
        setupRecyclerView()
        setupSearchView()
    }

    override fun setupClick() {
        setupFab()
        setupSettings()
        setupSort()
    }

    override fun observer() {
        observeNotes()
    }

    private fun setupSort() {
        binding.btnSort.click {
            val popup = it?.let { anchor -> PopupMenu(requireContext(), anchor) }
            popup?.menu?.add(0, 0, 0, getString(R.string.sort_newest))
            popup?.menu?.add(0, 1, 1, getString(R.string.sort_oldest))

            popup?.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    0 -> viewModel.setSortOrder(SortOrder.NEWEST_FIRST)
                    1 -> viewModel.setSortOrder(SortOrder.OLDEST_FIRST)
                }
                true
            }
            popup?.show()
        }
    }

    private fun setupSettings() {
        binding.btnSettings.click {
            val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvNotes.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchQueryChanged(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddNote.click {
            showAddNoteDialog()
        }
    }

    private fun observeNotes() {
        viewModel.notes.collectInStarted(this) { pagingData ->
            adapter.submitData(pagingData)
        }

        adapter.loadStateFlow.collectInStarted(this) { loadStates ->
            val isListEmpty =
                loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0
            if (isListEmpty) {
                binding.llEmptyState.visibleView()
                binding.rvNotes.goneView()
            } else {
                binding.llEmptyState.goneView()
                binding.rvNotes.visibleView()
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_new_note))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val title = dialogBinding.etTitle.text?.toString().orEmpty()
                val content = dialogBinding.etContent.text?.toString().orEmpty()
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