package com.example.healthconnect.codelab.data.model.ditto

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

//TODO: Delete file when not needed anymore
class DittoConnectionManager {
    /**
     * It takes the thingId and body and sends a PUT HTTP request to the Ditto server.
     * @param thingId A string corresponding to the Ditto Thing Id
     * @param body A string corresponding to the Ditto Thing Body
     * @return Response code of the HTTP PUT request
     */
    fun putDittoThing(thingId: String, body: String): Int {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.setRequestProperty("ngrok-skip-browser-warning", "")
        connection.doOutput = true

        OutputStreamWriter(connection.outputStream, "UTF-8").use {
            it.write(body)
            it.flush()
        }

        return connection.responseCode
    }

    /**
     * @param thingId A string corresponding to the Ditto Thing Id
     * @return A string with the response to the HTTP request
     */
    fun getDittoThing(thingId: String): String {
        return getHttpResponse(dittoThingConnection(thingId))
    }

    /**
     * @param thingId A string corresponding to the Ditto Thing Id
     * @return The response code of the HTTP request
     */
    fun existsDittoThing(thingId: String): Boolean {
        val responseCode = dittoThingConnection(thingId).responseCode
        return responseCode >= 100 && responseCode < 400
    }

    /**
     * @param thingId A string corresponding to the Ditto Thing Id
     * @return An HttpURLConnection object which contains all the connection information
     */
    private fun dittoThingConnection(thingId: String): HttpURLConnection {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.setRequestProperty("ngrok-skip-browser-warning", "")
        connection.doOutput = true
        return connection
    }

    /**
     * @param connection An HttpURLConnection with all the necessary information
     * @return A string with the response to that connection
     */
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

    /**
     * @param thingId A string corresponding to the Ditto Thing Id
     * @return Response code of the HTTP DELETE request
     */
    fun deleteDittoThing(thingId: String): Int {
        val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
        val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

        val uri = System.getProperty("DITTO_URL") + "/" + thingId
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic ${base64auth}")
        connection.setRequestProperty("ngrok-skip-browser-warning", "")
        connection.doOutput = true
        return connection.responseCode
    }
}