package com.example.healthconnect.codelab

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class PeriodicDittoService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        /*CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    val attributes = DittoAttributes("atr1", "atr2")
                    val feature1 = DittoFeature1("prop1", 3)
                    val features = DittoFeatures(feature1)
                    val dittoThing = DittoThing(attributes, features)

                    val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
                    val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

                    val url = URL(System.getProperty("DITTO_URL"))
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Authorization", "Basic ${base64auth}")
                    connection.doOutput = true

                    OutputStreamWriter(connection.outputStream, "UTF-8").use {
                        it.write(dittoThing.toJSON())
                        it.flush()
                    }

                    val code = connection.responseCode
                    Log.i("onStartJob", "Http request made!\tResponse code: ${code}")
                }
            } catch (e: Exception) {
                // Handle exceptions
                Log.i("onStartJob", e.toString())
            }
        }*/
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}