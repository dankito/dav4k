package net.dankito.dav.webdav.model

enum class Depth(val apiValue: String) {

    /**
     * Lists only the properties of the resource specified by url.
     *
     * Must be supported by server.
     */
    ResourceOnly("0"),

    /**
     * List the properties of the resource specified by url and its direct children (= directory listing).
     *
     * Must be supported by server.
     */
    DirectoryListing("1"),

    /**
     * Lists the properties of the resource specified by url and all of its direct and indirect children.
     *
     * May be supported by server. In practice, support for infinite-depth requests may be disabled, due to the
     * performance and security concerns associated with this behavior.
     *
     * A server may reject PROPFIND requests on collections with depth header of "Infinity" with 403 Forbidden.
     */
    Infinity("infinity"),

}