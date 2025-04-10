package net.dankito.dav.webdav.model

// TODO:
/*
<element name="error">
    <complexType>
        <sequence>
            <any namespace="##any"/>
        </sequence>
    </complexType>
</element>
 */
data class Error(
    val any: Any? = null,
)