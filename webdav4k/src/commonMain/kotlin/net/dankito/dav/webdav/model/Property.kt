package net.dankito.dav.webdav.model

import net.dankito.dav.DefaultNamespaces

data class Property(
    val name: String,
    val namespaceURI: String? = null,
    val prefix: String? = null
) {
    companion object {
        fun davProperty(name: String) = Property(name, DefaultNamespaces.Dav, DefaultNamespaces.DavPrefix)

        fun sabreDavProperty(name: String) = Property(name, DefaultNamespaces.SabreDav, DefaultNamespaces.SabreDavPrefix)

        fun ownCloudProperty(name: String) = Property(name, DefaultNamespaces.OwnCloud, DefaultNamespaces.OwnCloudPrefix)

        fun openCollaborationServicesProperty(name: String) = Property(name, DefaultNamespaces.OpenCollaborationServices, DefaultNamespaces.OpenCollaborationServicesPrefix)

        fun openCloudMeshProperty(name: String) = Property(name, DefaultNamespaces.OpenCloudMesh, DefaultNamespaces.OpenCloudMeshPrefix)
    }
}