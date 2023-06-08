package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class DetailChallengeResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("img")
	val img: String,

	@field:SerializedName("how many days")
	val howManyDays: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("end_time")
	val endTime: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("point")
	val point: Int,

	@field:SerializedName("desc")
	val desc: String
)
