package com.example.healthconnect.codelab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentHomeBinding
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).showNavbar(true)

        binding.toolbar.let {
            it.ivBack.visibility = View.GONE
            it.tvTitle.text = getString(R.string.title_home)
        }

        mainViewModel.userInformation.observe(viewLifecycleOwner) {
            binding.googleIdTv.text = it.googleId
            binding.nameTv.text = it.name
            binding.emailTv.text = it.email
            binding.imageView.setImageURI(it.profilePicture)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}