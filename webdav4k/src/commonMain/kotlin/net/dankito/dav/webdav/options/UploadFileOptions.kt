package net.dankito.dav.webdav.options

data class UploadFileOptions(
    val contentType: String? = null,
    val overwrite: Boolean = false
)