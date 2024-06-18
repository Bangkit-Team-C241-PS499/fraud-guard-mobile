package com.bangkit.fraudguard.ui.article


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
import com.bangkit.fraudguard.data.adapters.ArticleListAdapter
import com.bangkit.fraudguard.data.dto.response.ArticlesItem
import com.bangkit.fraudguard.databinding.FragmentArticleBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.facebook.shimmer.ShimmerFrameLayout


class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private lateinit var shimmerViewContainer: ShimmerFrameLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewModel()
        checkUserSession()
        binding.rvArticles.layoutManager = LinearLayoutManager(requireActivity())
        showShimmer()
        showArticle()
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
    private fun showArticle() {
        viewModel.getArticle().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                val articleList: List<ArticlesItem?>? = response.body()?.articles
                if (articleList != null) {
                    val adapter = ArticleListAdapter()
                    adapter.submitList(articleList)
                    binding.rvArticles.adapter = adapter
                    hideShimmer()

                } else {
                    hideShimmer()
                    showCustomToast(requireActivity(), "Gagal Memuat Artikel")


                    Log.e("HOMEFRAGMENT", "Article data null")
                }
            } else {
                hideShimmer()
                showCustomToast(requireActivity(), "Gagal Memuat Artikel")
                Log.e("HOMEFRAGMENT", "Failed to load article: ${response.errorBody()?.string()}")
            }
        })
    }

    private fun showShimmer() {
        binding.rvArticles.visibility = View.INVISIBLE
        var linearLayout = binding.shimmerViewContainer
        linearLayout.visibility = View.VISIBLE
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                child.startShimmer()
            }
        }

    }

    private fun hideShimmer() {
        var linearLayout = binding.shimmerViewContainer
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is ShimmerFrameLayout) {
                child.startShimmer()
            }
        }
        linearLayout.visibility = View.GONE

        binding.rvArticles.visibility = View.VISIBLE

    }



}