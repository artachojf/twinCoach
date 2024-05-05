package com.example.healthconnect.codelab.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentSettingsBinding
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.MainViewModel
import com.example.healthconnect.codelab.ui.settings.model.SettingsViewEntity
import com.example.healthconnect.codelab.utils.ViewUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var signInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(true)
            showToolbar(R.string.title_settings, false)
        }

        val settingsList =
            listOf(
                SettingsViewEntity.RegularSetting(getString(R.string.logout)) {
                    ViewUtils.showWarningDialog(requireContext(), R.string.logout_warning_dialog, ::onLogout, {})
                },
                SettingsViewEntity.DangerousSetting(getString(R.string.delete_account)) {
                    ViewUtils.showWarningDialog(requireContext(), R.string.delete_account_warning_dialog, ::onDeleteAccount, {})
                }
            )

        binding.apply {
            rvSettingOptions.layoutManager = LinearLayoutManager(context)
            rvSettingOptions.adapter = SettingsListAdapter(settingsList)
        }

        mainViewModel.userInformation.observe(viewLifecycleOwner) {
            if (it.googleId.isEmpty()) {
                (activity as MainActivity).apply {
                    showNavbar(false)
                    hideToolbar()
                }

                val action = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun onLogout() {
        signInClient.signOut()
        mainViewModel.onLogout()
    }

    private fun onDeleteAccount() {

    }
}