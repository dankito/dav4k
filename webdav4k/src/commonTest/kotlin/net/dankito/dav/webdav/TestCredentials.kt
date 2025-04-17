package net.dankito.dav.webdav

import net.dankito.web.client.KtorWebClient
import net.dankito.web.client.auth.Authentication

object TestCredentials {

    // TODO: set the URL of your WebDAV server (and its credentials) here:

    const val WebDavUrl = "<set url>"

    val credentials: Authentication? = null // may also set credentials

    fun createWebClient() = KtorWebClient(WebDavUrl, credentials)

}