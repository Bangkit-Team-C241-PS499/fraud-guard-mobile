package com.bangkit.fraudguard.ui.profile.profilePage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.FragmentProfileBinding
import com.bangkit.fraudguard.ui.customView.showCustomAlertDialog
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.detailProfile.DetailProfileActivity
import com.bangkit.fraudguard.ui.profile.editPassword.ChangePasswordActivity
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


        setupViewModel()
        checkUserSession()
        observeProfile()
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
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
            context?.showCustomAlertDialog(
                "Logout",
                "Apakah anda yakin ingin logout?",
                "Ya",
                "Tidak",
                {
                    mainViewModel.logout()
                    showCustomToast(requireContext(), "Berhasil Logout")
                },
                {}
            )

        }
        binding.rlListDetailUser.setOnClickListener {
            val intent = Intent(requireContext(), DetailProfileActivity::class.java)
            startActivity(intent)
        }

        binding.rlListGantiPassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.rlListHapusDeteksi.setOnClickListener {
            context?.showCustomAlertDialog(
                "Hapus Prediksi",
                "Apakah anda yakin ingin menghapus semua prediksi?",
                "Ya",
                "Tidak",
                {
                    deleteAllPrediction()
                },
                {}
            )
        }
        binding.rlListRiwayatDeteksi.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("fragmentToOpen", "history")
            }
            startActivity(intent)

        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireContext())
        ).get(MainViewModel::class.java)
    }

    private fun deleteAllPrediction(){
        mainViewModel.deleteAllPredictions().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                showCustomToast(requireContext(), "Berhasil menghapus semua prediksi")
            } else {
                showCustomToast(requireContext(), "Gagal menghapus semua prediksi")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        observeProfile()
    }

    private fun observeProfile() {
        mainViewModel.showProfile().observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                val profile: ProfileResponse? = response.body()
                // Update UI dengan data profile
                binding.profileEmail.text = profile?.email
                binding.profileName.text = profile?.name
                if (profile?.photoUrl != null) {
                    Glide.with(requireContext()).load(profile.photoUrl).into(binding.icon)
                }
            } else {
                showCustomToast(requireContext(), "Gagal memuat data profile")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}