package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.database.EventEntity
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.databinding.FragmentDetailBinding
import com.example.dicodingevent.viewmodel.MainViewModel
import com.example.dicodingevent.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val eventId = arguments?.getInt("eventId")
        if (eventId != null) {
            mainViewModel.getDetailEvent(eventId)
        }

        mainViewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            binding.apply {
                tvEventName.text = event.name
                tvEventOwner.text = event.ownerName
                tvEventTime.text = event.beginTime
                val remainingQuota = event.quota?.minus(event.registrants ?: 0)
                tvEventQuota.text = remainingQuota.toString()
                tvEventDescription.text = event.description?.let { description ->
                    HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
                btnRegister.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
                favoriteAdd.setOnClickListener {
                    handleFavoriteClick(event)
                }
            }

            Glide.with(requireContext())
                .load(event.mediaCover)
                .into(binding.ivEventImage)

            mainViewModel.checkFavoriteStatus(event.id.toString())
        }

        mainViewModel.favoriteStatus.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteAdd.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun handleFavoriteClick(event: Event) {
        val eventEntity = EventEntity(
            id = event.id.toString(),
            name = event.name,
            ownerName = event.ownerName,
            beginTime = event.beginTime,
            mediaCover = event.mediaCover,
            description = event.description,
            link = event.link,
            quota = event.quota,
            registrants = event.registrants,
            isActive = true
        )

        val currentFavoriteStatus = mainViewModel.favoriteStatus.value ?: false
        if (currentFavoriteStatus) {
            mainViewModel.removeFromFavorite(eventEntity)
            showSnackBar("Removed from favorites")
        } else {
            mainViewModel.addToFavorite(eventEntity)
            showSnackBar("Added to favorites")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}