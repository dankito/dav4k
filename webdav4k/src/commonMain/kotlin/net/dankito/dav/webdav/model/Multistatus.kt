package net.dankito.dav.webdav.model

data class MultiStatus(
    val responses: List<Response>,

    // did not find examples for these values
    val responseDescription: String? = null,
    val syncToken: String? = null,
)