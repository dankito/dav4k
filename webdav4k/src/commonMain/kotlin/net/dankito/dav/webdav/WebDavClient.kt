package net.dankito.dav.webdav

import net.dankito.dav.web.KtorWebClient
import net.dankito.dav.web.WebClient
import net.dankito.dav.web.credentials.Credentials
import net.dankito.dav.webdav.model.Depth
import net.dankito.dav.webdav.model.Property
import net.dankito.dav.webdav.operations.*
import net.dankito.dav.webdav.options.UploadFileOptions

open class WebDavClient(
    webClient: WebClient,
) {

    constructor(webDavUrl: String, credentials: Credentials? = null) : this(KtorWebClient(webDavUrl, credentials))


    protected open val propFind = PropFindHandler(webClient)

    protected open val createDirectory = CreateDirectoryCommand(webClient)

    protected open val downloadFile = DownloadFileCommand(webClient)

    protected open val uploadFile = UploadFileCommand(webClient)

    protected open val deleteFile = DeleteFileCommand(webClient)

    protected open val copyFile = CopyFileCommand(webClient)

    protected open val moveFile = MoveFileCommand(webClient)

    protected open val fileExists = FileExistsCommand(webClient)


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
    open suspend fun listResource(url: String, vararg props: Property) = list(url, Depth.ResourceOnly, *props)

    /**
     * Lists the properties of the resource specified by [url] and all its direct children.
     *
     * If no [props] are specified, then WebDAV `allprops` is used and server decides which properties to return, which
     * can be inefficient sometimes.
     *
     * @param url Relativ to webDavUrl specified when creating [WebDavClient] or absolute URL including protocol and host.
     * @param props The properties of the resource to return. If no properties are specified, then the server decides
     * which properties to return for the resource.
     */
    open suspend fun listDirectory(url: String, vararg props: Property) = list(url, Depth.DirectoryListing, *props)

    /**
     * Lists the properties of the resource specified by [url].
     *
     * [depth] controls how many sub hierarchy levels of the resource should be retrieved: the resource specified by
     * url only, the resource and its direct children (directory listing) or the resource and all its direct and
     * indirect children (can be rejected by server or lead to timeouts on large directories).
     *
     * If no [props] are specified, then WebDAV `allprops` is used and server decides which properties to return, which
     * can be inefficient sometimes.
     *
     * @param url Relativ to webDavUrl specified when creating [WebDavClient] or absolute URL including protocol and host.
     * @param depth - The resource specified by url only, the resource and its direct children (directory listing) or
     * the resource and all its direct and indirect children (can be rejected by server or lead to timeouts on large directories).
     * @param props The properties of each resource to return. If no properties are specified, then the server decides
     * which properties to return for each resource.
     */
    open suspend fun list(url: String, depth: Depth = PropFindHandler.DefaultDepth, vararg props: Property) =
        if (props.isEmpty()) propFind.allProp(url, depth)
        else propFind.prop(url, depth, *props)

    /**
     * Request a list of names of all the properties defined on the resource.
     *
     * But according to my experience does not work reliably. Some servers don't return any property names, others
     * only a few. Haven't found any server that really returned all available property names of a resource.
     */
    open suspend fun getAvailablePropertyNames(url: String, depth: Depth = PropFindHandler.DefaultDepth) =
        propFind.propName(url, depth)


    /**
     * Creates a directory at [url].
     */
    open suspend fun createDirectory(url: String) = createDirectory.createDirectory(url)

    /**
     * Downloads a file from [fileUrl].
     */
    open suspend fun downloadFile(fileUrl: String) = downloadFile.downloadFile(fileUrl)

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
    open suspend fun deleteFileOrDirectory(fileUrl: String) = deleteFile.deleteFile(fileUrl)

    /**
     * Copies a file from [sourceUrl] to [destinationUrl].
     *
     * Set [overwrite] to `true` to overwrite [destinationUrl] if it exists.
     */
    open suspend fun copyFile(sourceUrl: String, destinationUrl: String, overwrite: Boolean = false) =
        copyFile.copy(sourceUrl, destinationUrl, overwrite)

    /**
     * Moves a file from [sourceUrl] to [destinationUrl].
     *
     * Set [overwrite] to `true` to overwrite [destinationUrl] if it exists.
     */
    open suspend fun moveFile(sourceUrl: String, destinationUrl: String, overwrite: Boolean = false) =
        moveFile.move(sourceUrl, destinationUrl, overwrite)

    /**
     * Returns if the file at [fileUrl] exists.
     */
    open suspend fun fileExists(fileUrl: String) = fileExists.exists(fileUrl)

}