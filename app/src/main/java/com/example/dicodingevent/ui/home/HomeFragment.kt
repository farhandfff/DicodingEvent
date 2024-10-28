package com.example.dicodingevent.ui.home

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
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.ui.EventAdapter
import com.example.dicodingevent.viewmodel.MainViewModel
import com.example.dicodingevent.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapterVertical: EventAdapter
    private lateinit var adapterHorizontal: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        setupAdapter()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.apply {
            val horizontalLayout =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvHorizontal.layoutManager = horizontalLayout
            rvHorizontal.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    horizontalLayout.orientation
                )
            )
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
        adapterHorizontal = EventAdapter {
            val bundle = Bundle().apply {
                putInt("eventId", it)
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
        binding.apply {
            rvVertical.adapter = adapterVertical
            rvHorizontal.adapter = adapterHorizontal
        }
    }

    private fun observeViewModel() {
        binding.apply {
            mainViewModel.upcomingEvents.observe(viewLifecycleOwner) { listItems ->
                setUpcomingEvent(listItems)
            }

            mainViewModel.finishedEvents.observe(viewLifecycleOwner) { listItems ->
                setFinishedEvent(listItems)
            }
            mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }
            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                showError(errorMessage)
                btnRefresh.setOnClickListener {
                    mainViewModel.getFinishedEvents()
                    mainViewModel.getUpcomingEvents()
                }
            }

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
        val limitedList =
            if (listUpcomingEvent.size > 5) listUpcomingEvent.take(5) else listUpcomingEvent
        adapterHorizontal.submitList(limitedList)
    }

    private fun setFinishedEvent(listFinishedEvent: List<ListEventsItem>) {
        val limitedList =
            if (listFinishedEvent.size > 5) listFinishedEvent.takeLast(5) else listFinishedEvent
        adapterVertical.submitList(limitedList)
    }
}