package net.dankito.dav.webdav.model

data class Status(
    /**
     * The status line has this format:
     * ```
     * HTTP/<version> <status-code> <reason-phrase>
     * ```
     *
     * See [httpVersion], [httpStatusCode] and [reasonPhrase] for parsed values.
     */
    val statusLine: String,

    // the parsed statusLine
    val httpVersion: String? = null,
    val httpStatusCode: Int? = null,
    val reasonPhrase: String? = null,
) {
    val isSuccess: Boolean = httpStatusCode != null && httpStatusCode in 200..299
}