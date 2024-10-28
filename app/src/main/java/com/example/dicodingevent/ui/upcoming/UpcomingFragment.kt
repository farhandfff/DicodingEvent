package com.example.dicodingevent.ui.upcoming

import android.os.Bundle
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
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.ui.EventAdapter
import com.example.dicodingevent.viewmodel.MainViewModel
import com.example.dicodingevent.viewmodel.ViewModelFactory

class UpcomingFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapterVertical: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        setupAdapter()
        return binding.root
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
        adapterVertical = EventAdapter{
            val bundle = Bundle().apply {
                putInt("eventId", it)
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
        binding.rvVertical.adapter = adapterVertical
    }

    private fun observeViewModel() {
        mainViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            setUpcomingEvent(events)
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            showError(errorMessage)
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

    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        adapterVertical.submitList(listUpcomingEvent)
    }

}