package com.example.healthconnect.codelab.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this) {
            val list = listOf(R.id.homeFragment, R.id.loginFragment, R.id.splashFragment)
            if ((navController.currentDestination?.id ?: "") in list) finish()
            else navController.popBackStack()
        }

        viewModel.readUserInformation()

        viewModel.userInformation.observe(this) {
            setUserImage()
        }
    }

    fun showNavbar(bool: Boolean) {
        val visibility = if (bool) View.VISIBLE else View.GONE
        binding.navView.visibility = visibility
    }

    fun showToolbar(
        titleResId: Int,
        visibleArrow: Boolean = false
    ) {
        showToolbar(getString(titleResId), visibleArrow)
    }

    fun showToolbar(
        title: String,
        visibleArrow: Boolean = false
    ) {
        binding.toolbar.apply {
            tvTitle.text = title

            if (visibleArrow) ivBack.visibility = View.VISIBLE
            else ivBack.visibility = View.GONE
            ivBack.setOnClickListener {
                findNavController(R.id.nav_host_fragment_activity_main).popBackStack()
            }

            setUserImage()

            root.visibility = View.VISIBLE
        }
    }

    private fun setUserImage() {
        val userImageUrl = viewModel.userInformation.value?.profilePicture.toString()
        if (userImageUrl.isNotEmpty())
            Picasso.get().load(userImageUrl).into(binding.toolbar.ivUser)
    }

    fun hideToolbar() {
        binding.toolbar.root.visibility = View.GONE
    }
}
