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

	@field:SerializedName("img")
	val img: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("point")
	val point: Int
)
