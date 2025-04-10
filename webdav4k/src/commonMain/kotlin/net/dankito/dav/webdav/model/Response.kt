package net.dankito.dav.webdav.model

data class Response(
    //**t least one href must be stated (and in most cases it's exactly on href)
    val href: List<String>,
    // either propStats or status is set
    val propStats: List<PropStat>,
    val status: Status? = null,

    // did not find examples for these values
    val responseDescription: String? = null,
    val location: String? = null,
    val error: Error? = null,
)