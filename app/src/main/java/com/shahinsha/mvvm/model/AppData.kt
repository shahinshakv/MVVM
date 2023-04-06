package com.shahinsha.mvvm.model

data class AppData(
    val version: String,
    val base_code: String,
    val rates: Map<String, Double>
)
