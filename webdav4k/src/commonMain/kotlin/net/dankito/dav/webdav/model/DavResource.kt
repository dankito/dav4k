package net.dankito.dav.webdav.model

data class DavResource(
    val url: String,
    val properties: List<Property>,
    val notFoundProperties: List<Property> = emptyList(),
) {
    companion object {
        private fun findProperty(resource: DavResource, property: Property): String? =
            resource.properties.firstOrNull { it.name == property.name && it.namespaceUri == property.namespaceUri }?.value
    }


    val isFolder = properties.firstOrNull { it.name == Property.Dav.ResourceType.name && it.isDavProperty }
        ?.children.orEmpty().any { it.name == "collection" && it.isDavProperty }

    val isFile = isFolder == false


    val creationDate = findProperty(this, Property.Dav.CreationDate) // TODO: map to DateTime

    val displayname = findProperty(this, Property.Dav.Displayname)

    val contentLanguage = findProperty(this, Property.Dav.ContentLanguage)

    val contentLength = findProperty(this, Property.Dav.ContentLength)?.toIntOrNull()

    val contentType = findProperty(this, Property.Dav.ContentType)

    val etag = findProperty(this, Property.Dav.ETag)

    val lastModified = findProperty(this, Property.Dav.LastModified) // TODO: map to DateTime


    override fun toString() = "${if (isFolder) "Folder" else "File"} $url ${properties.size} properties: ${properties.joinToString { it.name }}"
}