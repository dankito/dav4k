package net.dankito.dav.webdav.model

data class Property(
    val name: String,
    val namespaceURI: String? = null,
    val prefix: String? = null
) {
    companion object {
        fun davProperty(name: String) = Property(name, "DAV:", "d")

        fun sabreDavProperty(name: String) = Property(name, "http://sabredav.org/ns", "s")

        fun ownCloudProperty(name: String) = Property(name, "http://owncloud.org/ns", "oc")

        fun openCollaborationServicesProperty(name: String) = Property(name, "http://open-collaboration-services.org/ns", "ocs")

        fun openCloudMeshProperty(name: String) = Property(name, "http://open-cloud-mesh.org/ns", "ocm")
    }
}