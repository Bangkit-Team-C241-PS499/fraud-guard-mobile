package com.bangkit.fraudguard.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.adapters.HistoryAdapter
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.databinding.FragmentHistoryBinding
import com.bangkit.fraudguard.databinding.FragmentHomeBinding
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity


class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewModel()
        checkUserSession()
        binding.rvHistoryPage.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistoryPage.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireActivity()).orientation
            )
        )
        showHistory()
        return root

    }
    private fun setupViewModel() {

        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireContext())
        ).get(MainViewModel::class.java)
    }
    private fun checkUserSession() {
        viewModel.getSession().observe(viewLifecycleOwner, Observer { userModel ->
            if (!userModel.isLogin) {
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        })
    }
    private fun showHistory() {
        viewModel.getHistory().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                val historyList: List<History>? = response.body()
                if (historyList != null) {
                    val adapter = HistoryAdapter()
                    adapter.submitList(historyList)
                    binding.rvHistoryPage.adapter = adapter

                } else {
                    Log.e("HOMEFRAGMENT", "History list is null")
                }
            } else {
                Log.e("HOMEFRAGMENT", "Failed to load history: ${response.errorBody()?.string()}")
            }
        })
    }

}