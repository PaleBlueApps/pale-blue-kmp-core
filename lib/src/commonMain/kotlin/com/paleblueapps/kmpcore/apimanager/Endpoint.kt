package com.paleblueapps.kmpcore.apimanager

import io.ktor.http.HttpMethod

data class Endpoint(
    val path: String,
    val method: HttpMethod,
)
