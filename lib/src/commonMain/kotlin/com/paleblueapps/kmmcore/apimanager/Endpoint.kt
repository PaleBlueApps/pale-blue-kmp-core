package com.paleblueapps.kmmcore.apimanager

import io.ktor.http.HttpMethod

data class Endpoint(
    val path: String,
    val method: HttpMethod,
)
