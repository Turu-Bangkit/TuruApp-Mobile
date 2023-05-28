package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class UserPointsResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("point")
	val point: String
)
