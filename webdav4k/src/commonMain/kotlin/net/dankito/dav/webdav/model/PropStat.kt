package net.dankito.dav.webdav.model

data class PropStat(
    val properties: List<Property>,
    val status: Status? = null,

    // did not find examples for these values
    val responseDescription: String? = null,
    val error: Error? = null,
)