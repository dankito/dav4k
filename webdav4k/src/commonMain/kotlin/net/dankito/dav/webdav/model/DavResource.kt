package net.dankito.dav.webdav.model

import net.dankito.datetime.Instant
import net.dankito.dav.DefaultNamespaces
import net.dankito.web.client.util.WebDateTimeUtil

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


    val creationDate = mapInstant(Property.Dav.CreationDate)

    val displayname = findPropertyValue(Property.Dav.Displayname)

    val contentLanguage = findPropertyValue(Property.Dav.ContentLanguage)

    val contentLength = findPropertyValue(Property.Dav.ContentLength)?.toLongOrNull()

    val contentType = findPropertyValue(Property.Dav.ContentType)

    val etag = findPropertyValue(Property.Dav.ETag)

    val lastModified = mapInstant(Property.Dav.LastModified)


    fun findDavPropertyValue(propertyName: String) = findPropertyValue(propertyName, DefaultNamespaces.Dav)

    fun findOwnCloudPropertyValue(propertyName: String) = findPropertyValue(propertyName, DefaultNamespaces.OwnCloud)

    private fun findPropertyValue(property: Property) = findPropertyValue(property.name, property.namespaceUri)

    fun findPropertyValue(propertyName: String, namespaceUri: String?): String? =
        findProperty(propertyName, namespaceUri)?.value

    fun findDavProperty(propertyName: String) = findProperty(propertyName, DefaultNamespaces.Dav)

    fun findOwnCloudProperty(propertyName: String) = findProperty(propertyName, DefaultNamespaces.OwnCloud)

    fun findProperty(propertyName: String, namespaceUri: String?): Property? =
        this.properties.firstOrNull { it.name == propertyName && it.namespaceUri == namespaceUri }

    private fun mapInstant(property: Property): Instant? = findPropertyValue(property)?.let { propertyValue ->
        WebDateTimeUtil.httpDateStringToInstantOrNull(propertyValue)
    }


    override fun toString() = "${if (isFolder) "Folder" else "File"} $url ${properties.size} properties: ${properties.joinToString { it.name }}"
}