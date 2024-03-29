package com.example.healthconnect.codelab

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthconnect.codelab.databinding.ActivityMainBinding
import com.example.healthconnect.codelab.dittoManager.PeriodicDittoService
import com.example.healthconnect.codelab.healthConnect.HealthConnectManager
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var hcManager: HealthConnectManager

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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
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

        lifecycleScope.launch {
            hcManager = HealthConnectManager(applicationContext)
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
        }
    }

    private fun launchService() {
        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
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