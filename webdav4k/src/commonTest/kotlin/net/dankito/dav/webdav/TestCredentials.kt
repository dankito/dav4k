package net.dankito.dav.webdav

import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.credentials.Credentials

object TestCredentials {

    // TODO: set the URL of your WebDAV server (and its credentials) here:

    const val WebDavUrl = "<set url>"

    val credentials: Credentials? = null // may also set credentials

    fun createWebClient() = KtorWebClient(WebDavUrl, credentials)

}