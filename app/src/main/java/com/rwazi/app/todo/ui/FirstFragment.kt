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
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    @javax.inject.Inject
    lateinit var authRepository: com.rwazi.app.todo.data.repository.AuthRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
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
            popup.menu.add(0, 0, 0, "Newest First")
            popup.menu.add(0, 1, 1, "Oldest First")
            
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
            onNoteClick = { /* Handle edit if needed */ },
            onNoteDelete = { note ->
                viewModel.deleteNote(note.id)
            }
        )
        binding.rvNotes.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<TextInputEditText>(R.id.etContent)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Note")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
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
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}