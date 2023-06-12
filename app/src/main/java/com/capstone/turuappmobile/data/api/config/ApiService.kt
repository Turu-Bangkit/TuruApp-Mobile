package com.capstone.turuappmobile.data.api.config

import com.capstone.turuappmobile.data.api.model.*
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
    @Field("token") tokenfirebase: String,
    ) : LoginResponse

    @GET("point/{id}")
    suspend fun getPoint(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : UserPointsResponse

    @FormUrlEncoded
    @POST("point/{id}")
    suspend fun addPoint(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("point") point: String
    ) : UserPointsResponse

    @FormUrlEncoded
    @POST("time/{id}")
    suspend fun sendSleepTime(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("sleeptime") sleepTime: String,
        @Field("endtime") endTime: String
    ) : BasicResponse

    @GET("challenge")
    suspend fun getAllChallenge(
        @Header("Authorization") token: String,
    ) : AllChallengeResponse

    @GET("challenge/{idChallenge}")
    suspend fun getDetailChallenge(
        @Header("Authorization") token: String,
        @Path("idChallenge") idChallenge: String,
    ) : DetailChallengeResponse

    @FormUrlEncoded
    @POST("chooseChallenge/{UIDUser}")
    suspend fun startChallenge(
        @Header("Authorization") token: String,
        @Path("UIDUser") UIDUser: String,
        @Field("idChallenge") idChallenge: String
    ): BasicResponse

    @GET("statusChallenge/{uid}")
    suspend fun getStatusChallenge(
        @Header("Authorization") token: String,
        @Path("uid") UIDUser: String,
    ): StatusChallengeResponse

    @FormUrlEncoded
    @POST("updateLevel/{UIDUser}")
    suspend fun updateLevelChallenge(
        @Header("Authorization") token: String,
        @Path("UIDUser") UIDUser: String,
        @Field("level") level: Int
    ): BasicResponse

    @GET("catalog")
    suspend fun getAllCatalog(
        @Header("Authorization") token: String,
    ) : AllCatalogRespone

    @GET("catalog/{idCatalog}")
    suspend fun getDetailCatalog(
        @Header("Authorization") token: String,
        @Path("idCatalog") idCatalog: String,
    ) : DetailCatalogResponse

    @POST("exchangePoint/{uid}")
    suspend fun exchangePoint(
        @Header("Authorization") token: String,
        @Path("uid") UIDUser: String,
        @Field("idCatalog") idCatalog: String
    ): BasicResponse

}