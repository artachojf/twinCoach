package com.example.healthconnect.codelab.data.service

import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DittoService {

    /* THINGS */

    @GET("/api/2/things/{thingId}")
    suspend fun retrieveCurrentStateThing(
        @Path("thingId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}")
    suspend fun retrieveGeneralInfoThing(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}/features")
    suspend fun retrieveGeneralInfoFeatures(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}/features/trainingPlan/properties")
    suspend fun retrieveSuggestedSessions(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}/features/goal/properties")
    suspend fun getGoalInformation(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}/attributes")
    suspend fun getAttributes(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/things/{googleId}/features/preferences/properties")
    suspend fun getGeneralInfoPreferences(
        @Path("googleId") googleId: String
    ): Response<ResponseBody>

    @GET("/api/2/search/things")
    suspend fun queryCurrentStateThings(
        @Query("filter") filter: String,
        @Query("option") option: String
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{thingId}")
    suspend fun putCurrentStateThing(
        @Path("thingId") thingId: String,
        @Body thing: DittoCurrentStateModel.Thing
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{googleId}")
    suspend fun putGeneralInfoThing(
        @Path("googleId") googleId: String,
        @Body thing: DittoGeneralInfoModel.Thing
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{googleId}/features")
    suspend fun putGeneralInfoFeatures(
        @Path("googleId") googleId: String,
        @Body features: DittoGeneralInfoModel.Features
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{googleId}/features/goal/properties")
    suspend fun putGeneralInfoGoal(
        @Path("googleId") googleId: String,
        @Body goal: DittoGeneralInfoModel.GoalProperties
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{thingId}/attributes")
    suspend fun putGeneralInfoAttributes(
        @Path("thingId") thingId: String,
        @Body attributes: DittoGeneralInfoModel.Attributes
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/api/2/things/{thingId}/features/preferences/properties")
    suspend fun putGeneralInfoPreferences(
        @Path("thingId") thingId: String,
        @Body attributes: DittoGeneralInfoModel.PreferencesProperties
    ): Response<ResponseBody>

    @DELETE("/api/2/things/{thingId}")
    suspend fun deleteThing(
        @Path("thingId") thingId: String
    ): Response<ResponseBody>

    /* POLICIES */

    @DELETE("/api/2/policies/{policyId}")
    suspend fun deletePolicy(
        @Path("policyId") policyId: String
    ): Response<ResponseBody>
}