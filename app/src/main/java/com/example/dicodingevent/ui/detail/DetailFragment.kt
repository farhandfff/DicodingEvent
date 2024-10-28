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
import com.example.dicodingevent.databinding.FragmentDetailBinding
import com.example.dicodingevent.viewmodel.MainViewModel
import com.example.dicodingevent.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Ambil eventId dari arguments
        val eventId = arguments?.getInt("eventId")
        if (eventId != null) {
            mainViewModel.getDetailEvent(eventId)
        }

        // Mengambil data event berdasarkan eventId yang diterima
        eventId?.let { id ->
            mainViewModel.isFavorite.observe(viewLifecycleOwner) { event ->
                // Update UI berdasarkan data event
            }
        }

        mainViewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            binding.apply {
                // Update tampilan UI dengan data event yang diterima
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
                    mainViewModel.toggleFavorite(event, requireContext())
                }
            }
            // Menampilkan gambar menggunakan Glide
            Glide.with(requireContext()).load(event.mediaCover).into(binding.ivEventImage)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}