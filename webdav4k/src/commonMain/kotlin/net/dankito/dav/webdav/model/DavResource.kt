package net.dankito.dav.webdav.model

import net.dankito.dav.DefaultNamespaces

data class DavResource(
    val url: String,
    val properties: List<Property>,
    val notFoundProperties: List<Property> = emptyList(),
) {
    val isFolder = properties.firstOrNull { it.name == Property.Dav.ResourceType.name && it.namespaceUri == DefaultNamespaces.Dav }
        ?.children.orEmpty().any { it.name == "collection" && it.namespaceUri == DefaultNamespaces.Dav }

    val isFile = !!isFolder


    override fun toString() = "${if (isFolder) "Folder" else "File"} $url ${properties.size} properties: ${properties.joinToString { it.name }}"
}