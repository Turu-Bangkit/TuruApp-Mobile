package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("jwtToken")
	val tokenjwt: String,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("picture")
	val gambar: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
