package com.example.healthconnect.codelab.ui

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.ActivityMainBinding
import com.example.healthconnect.codelab.services.PeriodicDittoService
import com.example.healthconnect.codelab.utils.healthConnect.HealthConnectManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.github.cdimascio.dotenv.dotenv
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //TODO: Move to viewModel when we know where to use it
    @Inject lateinit var hcManager: HealthConnectManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Environment variables
        val dotenv = dotenv {
            directory = "/assets"
            filename = "env"
        }
        dotenv.entries().forEach { System.setProperty(it.key, it.value) }

        /*lifecycleScope.launch {
            if (!hcManager.hasAllPermissions()) {
                val requestPermissions = registerForActivityResult(hcManager.requestPermissionActivityContract()) {
                    if (it.containsAll(hcManager.permissions)) launchService()
                    else {
                        showWarningDialog()
                    }
                }
                requestPermissions.launch(hcManager.permissions)
            } else {
                launchService()
            }
        }*/

        navController.addOnDestinationChangedListener { _,destination,_ ->
            val visibleArrow = !viewModel.noArrowFragments.contains(destination.id)
            supportActionBar?.setDisplayHomeAsUpEnabled(visibleArrow)
            showNavbar(visibleArrow || (destination.id == R.id.homeFragment))
        }

        onBackPressedDispatcher.addCallback(this) {
            if ((navController.currentDestination?.id ?: "") != R.id.homeFragment) navController.popBackStack()
        }

        viewModel.readUserInformation()
    }

    private fun showNavbar(bool: Boolean) {
        val visibility = if (bool) View.VISIBLE else View.GONE
        binding.navView.visibility = visibility
    }

    private fun launchService() {
        val jobScheduler = applicationContext.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobBuilder = JobInfo.Builder(1, ComponentName(applicationContext, PeriodicDittoService::class.java))
            .setPeriodic(1000*60*60).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        jobScheduler.schedule(jobBuilder.build())
    }

    private fun showWarningDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.permissions_alert_dialog_text)
        builder.setPositiveButton(R.string.permissions_alert_dialog_positive_button) { dialog, id ->
            //open application settings to grant permissions
        }
        builder.setNegativeButton(R.string.permissions_alert_dialog_negative_button) { dialog, id -> finish() }
        builder.show()
    }
}
