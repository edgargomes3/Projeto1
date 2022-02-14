package com.example.projeto1.retrofit

data class ValuesOutput(
    val success: Boolean
)

data class ErrorCHOOutput(
    val success: Boolean,
    val value: Float
)

data class ICROutput(
    val id_icr: Int,
    val icr_value: Int,
    val icr_date: String,
    val id_userProfile: Int
)

data class ISFOutput(
    val id_isf: Int,
    val isf_value: Int,
    val isf_date: String,
    val id_userProfile: Int
)

data class BolusOutput(
    val success: Boolean,
    val value: Float
)

data class GlicemiaOutput(
    val glycemia_value: Int,
    val glycemia_target: Int,
    val glycemia_limit_inf: Int,
    val glycemia_limit_sup: Int,
    val glycemia_date: String,
    val glycemia_datems: Int
)