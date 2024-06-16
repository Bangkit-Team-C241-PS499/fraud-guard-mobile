package com.bangkit.fraudguard.ui.create

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.data.dto.request.PredictRequest
import com.bangkit.fraudguard.databinding.FragmentCreateBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.history.HistoryDetailActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity

class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewModel()
        checkUserSession()
        setupAction()
        return root

    }

    private fun checkUserSession() {
        viewModel.getSession().observe(viewLifecycleOwner, Observer { userModel ->
            if (!userModel.isLogin) {
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        })
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireContext())
        ).get(MainViewModel::class.java)
    }
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setMyButtonEnable()
            val isTextEmpty = s.isNullOrEmpty()

            // Update the visibility of the button based on the text
            if (isTextEmpty) {
                hideBottomNavigationBarAndButton()
            } else {
                showBottomNavigationBarAndButton()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }
    private fun setMyButtonEnable(){
        val message = binding.textMessage.text.toString().isNotEmpty()
        binding.CheckButton.isEnabled = message
    }

    private fun setupAction(){
        binding.CheckCancelButton.setOnClickListener() {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.CheckButton.setOnClickListener(){
            val message = binding.textMessage.text.toString()
            val requestDTO = PredictRequest(message)
            viewModel.predict(requestDTO).observe(viewLifecycleOwner, Observer { response ->
                if(response.isSuccessful){
                    val body = response.body()
                    if(body != null){
                        val intent = Intent(requireContext(), HistoryDetailActivity::class.java).apply {
                            putExtra("id", body.id)
                        }
                        startActivity(intent)
                    }
                }
                else{
                    showCustomToast(requireContext(), "Prediksi gagal. Masukan teks yang valid.", Toast.LENGTH_SHORT)
                }
            })
        }
        binding.textMessage.addTextChangedListener(textWatcher)
        setMyButtonEnable()

        binding.textMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideBottomNavigationBarAndButton()
            } else {
                showBottomNavigationBarAndButton()
            }
        }

    }



    override fun onResume() {
        super.onResume()
        checkUserSession()
        binding.textMessage.text?.clear()
        setMyButtonEnable()
    }

    private fun hideBottomNavigationBarAndButton() {
        binding.CheckButton.visibility = View.GONE
        binding.CheckCancelButton.visibility = View.GONE
    }

    private fun showBottomNavigationBarAndButton() {
        binding.CheckButton.visibility = View.VISIBLE
        binding.CheckCancelButton.visibility = View.VISIBLE
    }
}