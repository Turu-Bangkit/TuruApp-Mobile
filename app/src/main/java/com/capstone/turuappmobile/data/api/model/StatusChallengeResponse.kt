package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class StatusChallengeResponse(

	@field:SerializedName("data")
	val data: DataStatusChallenge,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataStatusChallenge(

	@field:SerializedName("start_rules_time")
	val startRulesTime: Int,

	@field:SerializedName("end_rules_time")
	val endRulesTime: Int,

	@field:SerializedName("level_user")
	val levelUser: Int,

	@field:SerializedName("id_challenge")
	val idChallenge: String,

	@field:SerializedName("max_level")
	val maxLevel: Int
)
