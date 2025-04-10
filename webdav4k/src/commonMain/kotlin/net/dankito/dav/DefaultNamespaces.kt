package net.dankito.dav

object DefaultNamespaces {

    const val Dav = "DAV:"
    const val DavPrefix = "d"

    const val SabreDav = "http://sabredav.org/ns"
    const val SabreDavPrefix = "s"

    const val OwnCloud = "http://owncloud.org/ns"
    const val OwnCloudPrefix = "oc"

    const val OpenCollaborationServices = "http://open-collaboration-services.org/ns"
    const val OpenCollaborationServicesPrefix = "ocs"

    const val OpenCloudMesh = "http://open-cloud-mesh.org/ns"
    const val OpenCloudMeshPrefix = "ocm"


    val DefaultNamespacePrefixes = mapOf(
        Dav to DavPrefix,
        SabreDav to SabreDavPrefix,
        OwnCloud to OwnCloudPrefix,
        OpenCollaborationServices to OpenCollaborationServicesPrefix,
        OpenCloudMesh to OpenCloudMeshPrefix,
    )

    fun getPrefixForNamespace(namespaceUri: String): String? = DefaultNamespacePrefixes[namespaceUri]

}