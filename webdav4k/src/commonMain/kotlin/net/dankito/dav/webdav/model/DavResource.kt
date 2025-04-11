package net.dankito.dav.webdav.model

import net.dankito.dav.DefaultNamespaces

data class DavResource(
    val url: String,
    val properties: List<Property>,
    val notFoundProperties: List<Property> = emptyList(),
) {
    companion object {
    }


    val isFolder = findDavProperty(Property.Dav.ResourceType.name)
        ?.children.orEmpty().any { it.name == "collection" && it.isDavProperty }

    val isFile = isFolder == false


    val creationDate = findPropertyValue(Property.Dav.CreationDate) // TODO: map to DateTime

    val displayname = findPropertyValue(Property.Dav.Displayname)

    val contentLanguage = findPropertyValue(Property.Dav.ContentLanguage)

    val contentLength = findPropertyValue(Property.Dav.ContentLength)?.toLongOrNull()

    val contentType = findPropertyValue(Property.Dav.ContentType)

    val etag = findPropertyValue(Property.Dav.ETag)

    val lastModified = findPropertyValue(Property.Dav.LastModified) // TODO: map to DateTime


    fun findDavPropertyValue(propertyName: String) = findPropertyValue(propertyName, DefaultNamespaces.Dav)

    fun findOwnCloudPropertyValue(propertyName: String) = findPropertyValue(propertyName, DefaultNamespaces.OwnCloud)

    private fun findPropertyValue(property: Property) = findPropertyValue(property.name, property.namespaceUri)

    fun findPropertyValue(propertyName: String, namespaceUri: String?): String? =
        findProperty(propertyName, namespaceUri)?.value

    fun findDavProperty(propertyName: String) = findProperty(propertyName, DefaultNamespaces.Dav)

    fun findOwnCloudProperty(propertyName: String) = findProperty(propertyName, DefaultNamespaces.OwnCloud)

    fun findProperty(propertyName: String, namespaceUri: String?): Property? =
        this.properties.firstOrNull { it.name == propertyName && it.namespaceUri == namespaceUri }


    override fun toString() = "${if (isFolder) "Folder" else "File"} $url ${properties.size} properties: ${properties.joinToString { it.name }}"
}