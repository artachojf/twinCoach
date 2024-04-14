package com.example.healthconnect.codelab.data.service

import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface DittoService {

    @GET("/api/2/things/{thingId}")
    suspend fun retrieveCurrentStateThing(
        @Path("thingId") googleId: String
    ): DittoCurrentStateModel.Thing

    @GET("/api/2/things/{googleId}")
    suspend fun retrieveGeneralInfoThing(
        @Path("googleId") googleId: String
    ): DittoGeneralInfoModel.Thing

    @PUT("/api/2/things/{thingId}")
    suspend fun putCurrentStateThing(
        @Path("thingId") thingId: String,
        @Body thing: DittoCurrentStateModel.Thing
    )

    @PUT("/api/2/things/{googleId}")
    suspend fun putGeneralInfoThing(
        @Path("googleId") googleId: String,
        @Body thing: DittoGeneralInfoModel.Thing
    )

    @DELETE("/api/2/things{thingId}")
    suspend fun deleteThing(@Path("thingId") thingId: String)
}