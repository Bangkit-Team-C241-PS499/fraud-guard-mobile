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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.fraudguard.data.adapters.HistoryAdapter
import com.bangkit.fraudguard.data.adapters.SmallArticleListAdapter
import com.bangkit.fraudguard.data.dto.response.ArticlesItem
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.FragmentHomeBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.bumptech.glide.Glide
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
        binding.rvArticles.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        showShimmer()
        observeProfile()
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

    private fun observeProfile() {
        viewModel.showProfile().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                val profile: ProfileResponse? = response.body()
                // Update UI dengan data profile
                binding.headingTitle.text = "Halo, ${profile?.name}"
                if (profile?.photoUrl != null) {
                    Glide.with(requireContext()).load(profile.photoUrl).into(binding.icon)
                }
            } else {
                showCustomToast(requireContext(), "Gagal memuat data profile")
            }
        })
    }


    private fun setupAction() {
        binding.textViewSeeMore.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "history")
            }
            startActivity(intent)
        }

        binding.artikelSelengkapnya.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "article")
            }
            startActivity(intent)
        }
        binding.btnCekSpam.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "create")
            }
            startActivity(intent)
        }

        binding.icon.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "profile")
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
        showShimmerHistory(true)

        viewModel.getHistory().observe(viewLifecycleOwner, Observer { response ->

            if (response.isSuccessful) {
                showShimmerHistory(false)
                val historyList: List<History>? = response.body()
                if (historyList?.size!! > 0) {
                    showRvHistory(true)
                    showEmptyHistory(false)
                    val adapter = HistoryAdapter()
                    adapter.submitList(historyList)
                    binding.rvHistory.adapter = adapter

                } else if (historyList?.size == 0) {
                    showRvHistory(false)
                    showEmptyHistory(true)
                    Log.e("HOMEFRAGMENT", "History list is null")
                }
            } else {
                showCustomToast(requireActivity(), "Failed to load history")
                Log.e("HOMEFRAGMENT", "Failed to load history: ${response.errorBody()?.string()}")
            }
        })
    }


    private fun showArticles() {
        showShimmerArticle(true)
        viewModel.getArticle().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                showShimmerArticle(false)
                showRvArticles(true)

                val articleList: List<ArticlesItem?>? = response.body()?.articles
                if (articleList?.size!! > 0) {
                    val adapter = SmallArticleListAdapter()
                    adapter.submitList(articleList)
                    binding.rvArticles.adapter = adapter

                } else {
                    showCustomToast(requireActivity(), "Gagal Memuat Artikel")
                    Log.e("HOMEFRAGMENT", "Article list is null")
                }
            } else {
                showShimmerArticle(false)
                showCustomToast(requireActivity(), "Gagal Memuat Artikel")

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

    private fun showEmptyHistory(isShow: Boolean) {
        if (isShow) {
            binding.clEmptyHistory.visibility = View.VISIBLE
        } else {
            binding.clEmptyHistory.visibility = View.INVISIBLE
        }
    }

    private fun showShimmerArticle(isShow: Boolean) {
        var linearLayout = binding.rvArticlesShimmer
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                if (isShow) {
                    child.startShimmer()
                } else {
                    child.stopShimmer()
                }


            }
        }
        if (isShow) {
            linearLayout.visibility = View.VISIBLE
        } else {
            linearLayout.visibility = View.GONE
        }
    }

    private fun showShimmerHistory(isShow: Boolean){
        var linearLayout = binding.shimmerViewContainer
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                if (isShow) {
                    child.startShimmer()
                } else {
                    child.stopShimmer()
                }
            }
        }
        if (isShow) {
            linearLayout.visibility = View.VISIBLE
        } else {
            linearLayout.visibility = View.GONE
        }

    }

    private fun showRvHistory(isShow: Boolean){
        if (isShow) {
            binding.rvHistory.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.INVISIBLE
        }
    }

    private fun showRvArticles(isShow: Boolean){
        if (isShow) {
            binding.rvArticles.visibility = View.VISIBLE
        } else {
            binding.rvArticles.visibility = View.INVISIBLE
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}