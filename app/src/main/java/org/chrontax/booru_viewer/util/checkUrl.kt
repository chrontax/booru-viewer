package org.chrontax.booru_viewer.util

import java.net.MalformedURLException
import java.net.URL

fun checkUrl(url: String) {
    try {
        val url = URL(url)
        if (url.protocol != "http" && url.protocol != "https") {
            throw IllegalArgumentException("Invalid URL protocol: ${url.protocol}")
        }
    } catch (e: MalformedURLException) {
        throw IllegalArgumentException("Invalid URL: $url")
    }
}
