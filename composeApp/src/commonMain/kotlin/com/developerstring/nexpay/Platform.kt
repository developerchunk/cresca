package com.developerstring.nexpay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform