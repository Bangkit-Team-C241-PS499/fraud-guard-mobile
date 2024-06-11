package com.bangkit.fraudguard.ui.profile.profilePage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.FragmentProfileBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.detailProfile.DetailProfileActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.bumptech.glide.Glide

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        checkUserSession()
        observeProfile()
        setupAction()

        return root
    }

    private fun checkUserSession() {
        mainViewModel.getSession().observe(viewLifecycleOwner, Observer { userModel ->
            if (!userModel.isLogin) {
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        })


    }

    private fun setupAction() {
        binding.rlListLogout.setOnClickListener {
            mainViewModel.logout()
            showToast("Logout Success")
        }
        binding.rlListDetailUser.setOnClickListener {
            val intent = Intent(requireContext(), DetailProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()

    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireContext())
        ).get(MainViewModel::class.java)
    }

    private fun observeProfile() {
        mainViewModel.showProfile().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                showCustomToast(requireContext(), "Success to load profile")
                val profile: ProfileResponse? = response.body()
                // Update UI dengan data profile
                binding.profileEmail.text = profile?.email
                binding.profileName.text = profile?.name
                if (profile?.photoUrl != null) {
                    Glide.with(requireContext()).load(profile.photoUrl).into(binding.icon)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}