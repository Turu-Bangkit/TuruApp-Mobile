package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class AllChallengeResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataItem(

	@field:SerializedName("image")
	val img: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("point")
	val point: Int,

	@field:SerializedName("days")
	val days: Int,

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("end_time")
	val endTime: String,

	@field:SerializedName("desc")
	val desc: String,
)
