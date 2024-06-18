package com.bangkit.fraudguard.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fraudguard.data.adapters.HistoryAdapter
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.databinding.FragmentHistoryBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.facebook.shimmer.ShimmerFrameLayout


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
        setupAction()
        showHistory()
        return root

    }

    private fun setupAction() {
        binding.btnCekSpam.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "create")
            }
            startActivity(intent)
        }
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
        showShimmer(true)
        showRecyclerView(false)
        showEmptyHistoryList(false)

        viewModel.getHistory().observe(viewLifecycleOwner, Observer { response ->

            if (response.isSuccessful) {
                val historyList: List<History>? = response.body()
                showShimmer(false)

                if (historyList?.size != 0) {
                    showRecyclerView(true)
                    val adapter = HistoryAdapter()
                    adapter.submitList(historyList)
                    binding.rvHistoryPage.adapter = adapter
                } else if (historyList?.size == 0) {
                    showRecyclerView(false)
                    showEmptyHistoryList(true)
                    Log.e("HISTORYFRAGMENT", "History list is null")
                }
            } else {
                showCustomToast(requireActivity(), "Failed to load history")
                showShimmer(false)
                Log.e(
                    "HISTORYFRAGMENT",
                    "Failed to load history: ${response.errorBody()?.string()}"
                )
            }
        })
    }

    private fun showShimmer(isShow: Boolean) {

        if (isShow) {
            binding.shimmerViewContainer.visibility = View.VISIBLE

            var linearLayout = binding.shimmerViewContainer
            linearLayout.visibility = View.VISIBLE
            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is ShimmerFrameLayout) {
                    child.startShimmer()
                }
            }
        } else {
            binding.shimmerViewContainer.visibility = View.INVISIBLE
            for (i in 0 until binding.shimmerViewContainer.childCount) {
                val child = binding.shimmerViewContainer.getChildAt(i)
                if (child is ShimmerFrameLayout) {
                    child.stopShimmer()
                }
            }
        }
    }

    private fun showRecyclerView(isShow: Boolean) {
        if (isShow) {
            binding.rvHistoryPage.visibility = View.VISIBLE
        } else {
            binding.rvHistoryPage.visibility = View.INVISIBLE
        }
    }

    private fun showEmptyHistoryList(isShow: Boolean) {
        if (isShow) {
            binding.clEmptyHistory.visibility = View.VISIBLE
        } else {
            binding.clEmptyHistory.visibility = View.INVISIBLE
        }
    }


}