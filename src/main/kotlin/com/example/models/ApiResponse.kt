package com.example.models



import kotlinx.serialization.Serializable
import java.util.Collections.emptyList

@Serializable
data class ApiResponse(
    val success:Boolean,
    val message:String? = null,
    val prevPage:Int? = null,
    val nextPage:Int? = null,
    val heroes:List<Hero> = emptyList()
)
