package com.example.healthconnect.codelab.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentLoginBinding
import com.example.healthconnect.codelab.ui.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var signInClient: GoogleSignInClient

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            startForResult.launch(signInClient.signInIntent)
        }

        mainViewModel.userInformation.observe(viewLifecycleOwner) {
            if (it.googleId.isNotEmpty()) moveToHome()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            viewModel.onLogin(account)
            moveToHome()
        } else {
            Toast.makeText(requireContext(), R.string.login_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun moveToHome() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}