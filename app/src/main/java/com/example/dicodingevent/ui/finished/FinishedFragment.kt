package com.example.dicodingevent.ui.finished

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.EventAdapter
import com.example.dicodingevent.viewmodel.MainViewModel
import com.example.dicodingevent.viewmodel.ViewModelFactory

class FinishedFragment : Fragment() {

    private lateinit var binding: FragmentFinishedBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapterVertical: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        setupAdapter()
        setupSearchView()

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Log.d("Finished", error)
        }

        return binding.root
    }

    private fun setupSearchView() {
        binding.apply {
            searchView.setupWithSearchBar(binding.searchBar)

            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val keyword = searchView.text.toString()
                mainViewModel.searchEvent(keyword)

                val currentText = searchView.text

                searchView.hide()

                searchView.editText.text = currentText

                true
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            val verticalLayout = LinearLayoutManager(requireContext())
            rvVertical.layoutManager = verticalLayout
            rvVertical.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    verticalLayout.orientation
                )
            )
        }
    }

    private fun setupAdapter() {
        adapterVertical = EventAdapter {
            val bundle = Bundle().apply {
                putInt("eventId", it)
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
        binding.rvVertical.adapter = adapterVertical
    }

    private fun observeViewModel() {
        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            setFinishedEvents(events)
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            showError(errorMessage)
        }
        mainViewModel.searchEvent.observe(viewLifecycleOwner) {listItem ->
            setFinishedEvents(listItem)
        }
    }

    private fun showError(errorMessage: String) {
        binding.tvError.visibility = View.VISIBLE
        binding.btnRefresh.visibility = View.VISIBLE
        binding.tvError.text = errorMessage
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setFinishedEvents(listFinishedEvent: List<ListEventsItem>) {
        adapterVertical.submitList(listFinishedEvent)
    }

}