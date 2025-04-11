package net.dankito.dav.webdav.model

data class DavResource(
    val url: String,
    val properties: List<Property>,
    val notFoundProperties: List<Property> = emptyList(),
) {
    val isFolder = properties.firstOrNull { it.name == Property.Dav.ResourceType.name && it.isDavProperty }
        ?.children.orEmpty().any { it.name == "collection" && it.isDavProperty }

    val isFile = isFolder == false


    override fun toString() = "${if (isFolder) "Folder" else "File"} $url ${properties.size} properties: ${properties.joinToString { it.name }}"
}