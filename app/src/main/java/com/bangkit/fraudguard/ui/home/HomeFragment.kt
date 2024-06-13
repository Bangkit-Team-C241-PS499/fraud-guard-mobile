package com.bangkit.fraudguard.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fraudguard.data.adapters.HistoryAdapter
import com.bangkit.fraudguard.data.adapters.SmallArticleListAdapter
import com.bangkit.fraudguard.data.dto.response.ArticlesItem
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.databinding.FragmentHomeBinding
import com.bangkit.fraudguard.ui.history.HistoryFragment
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.detailProfile.DetailProfileActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.facebook.shimmer.ShimmerFrameLayout

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root
        setupViewModel()
        checkUserSession()
        binding.rvArticles.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireActivity()).orientation
            )
        )
        showShimmer()
        showHistory()
        showArticles()
        setupAction()
        return root
    }

    private fun setupViewModel() {

        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireContext())
        ).get(MainViewModel::class.java)
    }


    private fun setupAction(){
        binding.textViewSeeMore.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "history")
            }
            startActivity(intent)
        }
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
        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        viewModel.getHistory().observe(viewLifecycleOwner, Observer { response ->
            binding.progressBar.visibility = View.GONE // Hide progress bar when data is loaded

            if (response.isSuccessful) {
                val historyList: List<History>? = response.body()
                if (historyList != null) {
                    val adapter = HistoryAdapter()
                    adapter.submitList(historyList)
                    binding.rvHistory.adapter = adapter
                } else {
                    Log.e("HOMEFRAGMENT", "History list is null")
                }
            } else {
                Log.e("HOMEFRAGMENT", "Failed to load history: ${response.errorBody()?.string()}")
            }
        })
    }


    private fun showArticles() {
        viewModel.getArticle().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                hideShimmer()
                val articleList: List<ArticlesItem?>? = response.body()?.articles
                if (articleList != null) {
                    val adapter = SmallArticleListAdapter()
                    adapter.submitList(articleList)
                    binding.rvArticles.adapter = adapter

                } else {
                    Log.e("HOMEFRAGMENT", "Article list is null")
                }
            } else {
                hideShimmer()
                Log.e("HOMEFRAGMENT", "Failed to load Article: ${response.errorBody()?.string()}")
            }
        })
    }

    private fun showShimmer() {
        binding.rvArticles.visibility = View.INVISIBLE
        var linearLayout = binding.rvArticlesShimmer
        linearLayout.visibility = View.VISIBLE
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                child.startShimmer()
            }
        }

    }

    private fun hideShimmer() {
        var linearLayout = binding.rvArticlesShimmer
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                child.startShimmer()
            }
        }
        linearLayout.visibility = View.GONE

        binding.rvArticles.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}