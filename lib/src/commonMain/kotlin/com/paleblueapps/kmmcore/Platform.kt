package com.paleblueapps.kmmcore

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform