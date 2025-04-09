package net.dankito.dav.web.credentials

/**
 * BasicAuth is by default disabled in OpenCloud. So if you like to use BasicAuth as authentication mechanism you will
 * first have to enable it by setting
 * ```
 * PROXY_ENABLE_BASIC_AUTH: "true"
 * ```
 * in OpenCloud config.
 */
open class BasicAuthCredentials(
    val username: String,
    val password: String,
) : Credentials {
    override fun toString() = "Basic Auth credentials for user $username with password ${password.substring(0, 2).padEnd(password.length - 2, '*')}"
}