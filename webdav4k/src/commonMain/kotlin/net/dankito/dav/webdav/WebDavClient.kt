package net.dankito.dav.webdav

import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.WebClient
import net.dankito.dav.web.credentials.Credentials
import net.dankito.dav.webdav.methods.PropFindHandler

open class WebDavClient(
    webClient: WebClient,
) {

    constructor(webDavUrl: String, credentials: Credentials? = null) : this(KtorWebClient(webDavUrl, credentials))


    protected open val propFindHandler = PropFindHandler(webClient)


    open suspend fun list(url: String, depth: Int = 1) = propFindHandler.list(url, depth)

}