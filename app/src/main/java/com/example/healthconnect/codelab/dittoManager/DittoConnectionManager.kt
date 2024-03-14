package com.example.healthconnect.codelab.dittoManager

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Base64

class DittoConnectionManager {
    fun generateDittoThingId(instant: Instant): String {
        return System.getProperty("DITTO_THING_PREFIX") + ":" + System.getProperty("GOOGLE_ID") + "-" +
                instant.atZone(ZoneOffset.systemDefault()).toLocalDate().toString()
    }

    fun generateDittoThingId(localDate: LocalDate): String {
        return System.getProperty("DITTO_THING_PREFIX") + ":" + System.getProperty("GOOGLE_ID") + "-" +
                localDate.toString()
    }

    fun putDittoThing(thingId: String, body: String): Int {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.doOutput = true

        OutputStreamWriter(connection.outputStream, "UTF-8").use {
            it.write(body)
            it.flush()
        }

        return connection.responseCode
    }

    fun getDittoThing(thingId: String): String {
        return getHttpResponse(dittoThingConnection(thingId))
    }

    fun existsDittoThing(thingId: String): Boolean {
        val responseCode = dittoThingConnection(thingId).responseCode
        return responseCode >= 100 && responseCode < 400
    }

    private fun dittoThingConnection(thingId: String): HttpURLConnection {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.doOutput = true
        return connection
    }

    private fun getHttpResponse(connection: HttpURLConnection): String {
        val stringBuffer = StringBuffer()
        val br: BufferedReader = if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
            BufferedReader(InputStreamReader(connection.getInputStream()))
        } else {
            BufferedReader(InputStreamReader(connection.getErrorStream()))
        }

        var currentLine: String? = br.readLine()
        while(currentLine != null) {
            stringBuffer.append(currentLine)
            currentLine = br.readLine()
        }

        return stringBuffer.toString()
    }

    fun deleteDittoThing(thingId: String): Int {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.doOutput = true
        return connection.responseCode
    }
}