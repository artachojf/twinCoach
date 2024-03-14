package com.example.healthconnect.codelab

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthconnect.codelab.databinding.ActivityMainBinding
import io.github.cdimascio.dotenv.dotenv

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        //PeriodicDittoService launching: once per hour
        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobBuilder = JobInfo.Builder(1, ComponentName(this, PeriodicDittoService::class.java))
        val sched = jobScheduler.schedule(jobBuilder.setPeriodic(1000*60*60).build())
        Log.i("scheduling", "${sched == JobScheduler.RESULT_SUCCESS}")
    }
}