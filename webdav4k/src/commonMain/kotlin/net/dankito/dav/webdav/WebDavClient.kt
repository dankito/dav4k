package net.dankito.dav.webdav

import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.WebClient
import net.dankito.dav.web.credentials.Credentials
import net.dankito.dav.webdav.model.Property
import net.dankito.dav.webdav.operations.DeleteFileCommand
import net.dankito.dav.webdav.operations.PropFindHandler
import net.dankito.dav.webdav.operations.UploadFileCommand
import net.dankito.dav.webdav.options.UploadFileOptions

open class WebDavClient(
    webClient: WebClient,
) {

    constructor(webDavUrl: String, credentials: Credentials? = null) : this(KtorWebClient(webDavUrl, credentials))


    protected open val propFind = PropFindHandler(webClient)

    protected open val uploadFile = UploadFileCommand(webClient)

    protected open val deleteFile = DeleteFileCommand(webClient)


    /**
     * Lists the properties of the resource specified by [url].
     *
     * If no [props] are specified, then WebDAV `allprops` is used and server decides which properties to return, which
     * can be inefficient sometimes.
     *
     * @param url Relativ to webDavUrl specified when creating [WebDavClient] or absolute URL including protocol and host.
     * @param props The properties of the resource to return. If no properties are specified, then the server decides
     * which properties to return for the resource.
     */
    open suspend fun list(url: String, vararg props: Property) = list(url, 0, *props)

    /**
     * Lists the properties of the resource specified by [url].
     *
     * [depth] controls how many sub hierarchy levels of the resource should be retrieved. 0 = only the resource, 1 =
     * the resource and its direct children (directory listing), [Int.MAX_VALUE] or < 0 = all children (can be rejected
     * by server or lead to timeouts on large directories).
     *
     * If no [props] are specified, then WebDAV `allprops` is used and server decides which properties to return, which
     * can be inefficient sometimes.
     *
     * @param url Relativ to webDavUrl specified when creating [WebDavClient] or absolute URL including protocol and host.
     * @param depth - 0 = only the resource specified by url. - 1 = directory listing (the resource and its direct
     * children). - > 1 = even more child levels. - [Int.MAX_VALUE] or < 0 = all resources which some servers will
     * reject or on large directories may will lead to timeouts as the server is not able to compute the result in time.
     * @param props The properties of each resource to return. If no properties are specified, then the server decides
     * which properties to return for each resource.
     */
    open suspend fun list(url: String, depth: Int = PropFindHandler.DefaultDepth, vararg props: Property) =
        if (props.isEmpty()) propFind.allProp(url, depth)
        else propFind.prop(url, depth, *props)


    /**
     * Uploads a file to [destinationUrl]. Ensure that all intermediate folders exist on server.
     *
     * [UploadFileOptions.overwrite] currently does not work.
     */
    open suspend fun uploadFile(destinationUrl: String, fileContent: ByteArray, options: UploadFileOptions? = null) =
        uploadFile.uploadFile(destinationUrl, fileContent, options)

    /**
     * Deletes the file or directory identified by [fileUrl].
     */
    open suspend fun deleteFile(fileUrl: String) = deleteFile.deleteFile(fileUrl)

}