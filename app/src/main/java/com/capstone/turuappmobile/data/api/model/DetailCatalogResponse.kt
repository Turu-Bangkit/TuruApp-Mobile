package com.capstone.turuappmobile.data.api.model

import com.google.gson.annotations.SerializedName

data class DetailCatalogResponse(

	@field:SerializedName("data")
	val data: DataDetailCatalog,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataDetailCatalog(

	@field:SerializedName("img")
	val img: String,

	@field:SerializedName("price")
	val price: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("point")
	val point: Int
)
