package com.dts.webapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetUsers {
    @GET("/api/Users/GetUsers")
    fun GetUsers(
        @Query("Empresa") Empresa: Int)
            : Call<List<ClassesAPI.clsAPIUsers?>?>?
}