package net.dankito.dav.webdav.model

import net.dankito.dav.DefaultNamespaces

data class Property(
    val name: String,
    val namespaceUri: String? = null,
    val prefix: String? = null,

    val value: String? = null,
    val children: List<Property> = emptyList(),
) {
    companion object {
        fun davProperty(name: String) = Property(name, DefaultNamespaces.Dav, DefaultNamespaces.DavPrefix)

        fun sabreDavProperty(name: String) = Property(name, DefaultNamespaces.SabreDav, DefaultNamespaces.SabreDavPrefix)

        fun ownCloudProperty(name: String) = Property(name, DefaultNamespaces.OwnCloud, DefaultNamespaces.OwnCloudPrefix)

        fun openCollaborationServicesProperty(name: String) = Property(name, DefaultNamespaces.OpenCollaborationServices, DefaultNamespaces.OpenCollaborationServicesPrefix)

        fun openCloudMeshProperty(name: String) = Property(name, DefaultNamespaces.OpenCloudMesh, DefaultNamespaces.OpenCloudMeshPrefix)


        /**
         * See https://www.rfc-editor.org/rfc/rfc4918#section-15
         *
         * Or less information but better readable: https://www.vias.org/encyclopedia/rfc2518_011.html
         */
        object Dav {
            /**
             * Purpose:   Records the time and date the resource was created.
             *
             * Value:   date-time (defined in [RFC3339], see the ABNF in Section
             *   5.6.)
             *
             * Protected:   MAY be protected.  Some servers allow DAV:creationdate
             *   to be changed to reflect the time the document was created if that
             *   is more meaningful to the user (rather than the time it was
             *   uploaded).  Thus, clients SHOULD NOT use this property in
             *   synchronization logic (use DAV:getetag instead).
             *
             * COPY/MOVE behavior:   This property value SHOULD be kept during a
             *   MOVE operation, but is normally re-initialized when a resource is
             *   created with a COPY.  It should not be set in a COPY.
             *
             * Description:   The DAV:creationdate property SHOULD be defined on all
             *   DAV compliant resources.  If present, it contains a timestamp of
             *   the moment when the resource was created.  Servers that are
             *   incapable of persistently recording the creation date SHOULD
             *   instead leave it undefined (i.e. report "Not Found").
             */
            val CreationDate = davProperty("creationdate")

            /**
             * Purpose:   Provides a name for the resource that is suitable for
             *   presentation to a user.
             *
             * Value:   Any text.
             *
             * Protected:   SHOULD NOT be protected.  Note that servers implementing
             *   [RFC2518] might have made this a protected property as this is a
             *   new requirement.
             *
             * COPY/MOVE behavior:   This property value SHOULD be preserved in COPY
             *   and MOVE operations.
             *
             * Description:   Contains a description of the resource that is
             *   suitable for presentation to a user.  This property is defined on
             *   the resource, and hence SHOULD have the same value independent of
             *   the Request-URI used to retrieve it (thus, computing this property
             *   based on the Request-URI is deprecated).  While generic clients
             *   might display the property value to end users, client UI designers
             *   must understand that the method for identifying resources is still
             *   the URL.  Changes to DAV:displayname do not issue moves or copies
             *   to the server, but simply change a piece of meta-data on the
             *   individual resource.  Two resources can have the same DAV:
             *   displayname value even within the same collection.
             */
            val Displayname = davProperty("displayname")

            /**
             * Purpose:   Contains the Content-Language header value (from Section
             *   14.12 of [RFC2616]) as it would be returned by a GET without
             *   accept headers.
             *
             * Value:   language-tag (language-tag is defined in Section 3.10 of
             *   [RFC2616])
             *
             * Protected:   SHOULD NOT be protected, so that clients can reset the
             *   language.  Note that servers implementing [RFC2518] might have
             *   made this a protected property as this is a new requirement.
             *
             * COPY/MOVE behavior:   This property value SHOULD be preserved in COPY
             *   and MOVE operations.
             *
             * Description:   The DAV:getcontentlanguage property MUST be defined on
             *   any DAV-compliant resource that returns the Content-Language
             *   header on a GET.
             */
            val ContentLanguage = davProperty("getcontentlanguage")

            /**
             * Purpose:   Contains the Content-Length header returned by a GET
             *   without accept headers.
             *
             * Value:   See Section 14.13 of [RFC2616].
             *
             * Protected:   This property is computed, therefore protected.
             *
             * Description:   The DAV:getcontentlength property MUST be defined on
             *   any DAV-compliant resource that returns the Content-Length header
             *   in response to a GET.
             *
             * COPY/MOVE behavior:   This property value is dependent on the size of
             *   the destination resource, not the value of the property on the
             *   source resource.
             */
            val ContentLength = davProperty("getcontentlength")

            /**
             * Purpose:   Contains the Content-Type header value (from Section 14.17
             *   of [RFC2616]) as it would be returned by a GET without accept
             *   headers.
             *
             * Value:   media-type (defined in Section 3.7 of [RFC2616])
             *
             * Protected:   Potentially protected if the server prefers to assign
             *   content types on its own (see also discussion in Section 9.7.1).
             *
             * COPY/MOVE behavior:   This property value SHOULD be preserved in COPY
             *   and MOVE operations.
             *
             * Description:   This property MUST be defined on any DAV-compliant
             *   resource that returns the Content-Type header in response to a
             *   GET.
             */
            val ContentType = davProperty("getcontenttype")

            /**
             * Purpose:   Contains the ETag header value (from Section 14.19 of
             *   [RFC2616]) as it would be returned by a GET without accept
             *   headers.
             *
             * Value:   entity-tag (defined in Section 3.11 of [RFC2616])
             *
             * Protected:  MUST be protected because this value is created and
             *   controlled by the server.
             *
             * COPY/MOVE behavior:   This property value is dependent on the final
             *   state of the destination resource, not the value of the property
             *   on the source resource.  Also note the considerations in
             *   Section 8.8.
             *
             * Description:   The getetag property MUST be defined on any DAV-
             *   compliant resource that returns the Etag header.  Refer to Section
             *   3.11 of RFC 2616 for a complete definition of the semantics of an
             *   ETag, and to Section 8.6 for a discussion of ETags in WebDAV.
             */
            val ETag = davProperty("getetag")

            /**
             * Purpose:   Contains the Last-Modified header value (from Section
             *   14.29 of [RFC2616]) as it would be returned by a GET method
             *   without accept headers.
             *
             * Value:   rfc1123-date (defined in Section 3.3.1 of [RFC2616])
             *
             * Protected:   SHOULD be protected because some clients may rely on the
             *   value for appropriate caching behavior, or on the value of the
             *   Last-Modified header to which this property is linked.
             *
             *
             * COPY/MOVE behavior:   This property value is dependent on the last
             *   modified date of the destination resource, not the value of the
             *   property on the source resource.  Note that some server
             *   implementations use the file system date modified value for the
             *   DAV:getlastmodified value, and this can be preserved in a MOVE
             *   even when the HTTP Last-Modified value SHOULD change.  Note that
             *   since [RFC2616] requires clients to use ETags where provided, a
             *   server implementing ETags can count on clients using a much better
             *   mechanism than modification dates for offline synchronization or
             *   cache control.  Also note the considerations in Section 8.8.
             *
             * Description:   The last-modified date on a resource SHOULD only
             *   reflect changes in the body (the GET responses) of the resource.
             *   A change in a property only SHOULD NOT cause the last-modified
             *   date to change, because clients MAY rely on the last-modified date
             *   to know when to overwrite the existing body.  The DAV:
             *   getlastmodified property MUST be defined on any DAV-compliant
             *   resource that returns the Last-Modified header in response to a
             *   GET.
             */
            val LastModified = davProperty("getlastmodified")

            /**
             * Purpose:   Describes the active locks on a resource
             *
             * Protected:   MUST be protected.  Clients change the list of locks
             *   through LOCK and UNLOCK, not through PROPPATCH.
             *
             * COPY/MOVE behavior:   The value of this property depends on the lock
             *   state of the destination, not on the locks of the source resource.
             *   Recall that locks are not moved in a MOVE operation.
             *
             * Description:   Returns a listing of who has a lock, what type of lock
             *   he has, the timeout type and the time remaining on the timeout,
             *   and the associated lock token.  Owner information MAY be omitted
             *   if it is considered sensitive.  If there are no locks, but the
             *   server supports locks, the property will be present but contain
             *   zero 'activelock' elements.  If there are one or more locks, an
             *   'activelock' element appears for each lock on the resource.  This
             *   property is NOT lockable with respect to write locks (Section 7).
             */
            val LockDiscovery = davProperty("lockdiscovery")

            /**
             * Purpose:   Specifies the nature of the resource.
             *
             * Protected:   SHOULD be protected.  Resource type is generally decided
             *   through the operation creating the resource (MKCOL vs PUT), not by
             *   PROPPATCH.
             *
             * COPY/MOVE behavior:   Generally a COPY/MOVE of a resource results in
             *   the same type of resource at the destination.
             *
             * Description:   MUST be defined on all DAV-compliant resources.  Each
             *   child element identifies a specific type the resource belongs to,
             *   such as 'collection', which is the only resource type defined by
             *   this specification (see Section 14.3).  If the element contains
             *   the 'collection' child element plus additional unrecognized
             *   elements, it should generally be treated as a collection.  If the
             *   element contains no recognized child elements, it should be
             *   treated as a non-collection resource.  The default value is empty.
             *   This element MUST NOT contain text or mixed content.  Any custom
             *   child element is considered to be an identifier for a resource
             *   type.
             */
            val ResourceType = davProperty("resourcetype")

            /**
             * Purpose:   To provide a listing of the lock capabilities supported by
             *   the resource.
             *
             * Protected:   MUST be protected.  Servers, not clients, determine what
             *   lock mechanisms are supported.
             *
             * COPY/MOVE behavior:   This property value is dependent on the kind of
             *   locks supported at the destination, not on the value of the
             *   property at the source resource.  Servers attempting to COPY to a
             *   destination should not attempt to set this property at the
             *   destination.
             *
             * Description:   Returns a listing of the combinations of scope and
             *   access types that may be specified in a lock request on the
             *   resource.  Note that the actual contents are themselves controlled
             *   by access controls, so a server is not required to provide
             *   information the client is not authorized to see.  This property is
             *   NOT lockable with respect to write locks (Section 7).
             */
            val SupportedLock = davProperty("supportedlock")
        }

        /**
         * See https://sabre.io/dav/xmlelements/
         */
        object SabreDav {
            /**
             * This property is defined on Principals. If you are making use of
             * Sabre\DAVACL\AbstractPrincipalCollection / Sabre\DAVACL\Principal this property may be defined.
             * It contains the email address of a principal.
             */
            val EmailAddress = sabreDavProperty("email-address")

            /**
             * This property is added by the [TemporaryFileFilterPlugin](https://sabre.io/dav/temporary-files).
             * If a PROPFIND is done on a file that's matched by the TemporaryFileFilter, this property will contain 'true'.
             *
             * This property also appears in 'allprops' responses.
             */
            val TempFile = sabreDavProperty("tempFile")

            /**
             * This property is defined on principals. It contains a relative url, pointing to the users' own vcard.
             * This property maps to Addressbook's {http://calendarserver.org/ns/}me-card property. The reason for
             * making this into a separate property, is because this me-card property is defined on the addressbook-home,
             * which doesn't make a ton of sense.
             */
            val VCardUrl = sabreDavProperty("vcard-url")


            // there are additional XML elements defined for error messages, see https://sabre.io/dav/xmlelements/
        }
    }
}