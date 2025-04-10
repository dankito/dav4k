package net.dankito.dav.webdav

import assertk.assertThat
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.dankito.dav.webdav.model.Property
import kotlin.test.Test

class WebDavClientTest {

    private val underTest = WebDavClient(TestCredentials.createWebClient())


    @Test
    fun list_DefaultProperties() = runTest {
        val result = underTest.list("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe")

        assertThat(result).isNotNull()
    }

    @Test
    fun list_SpecifyWhichPropertiesToReturn() = runTest {
        val result = underTest.list("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe",
            // builds this ownCloud example: https://owncloud.dev/apis/http/webdav/#listing-properties
            Property.ownCloudProperty("permissions"), Property.ownCloudProperty("favorite"),
            Property.ownCloudProperty("fileid"),
            Property.ownCloudProperty("owner-id"), Property.ownCloudProperty("owner-display-name"),
            Property.ownCloudProperty("share-types"), Property.ownCloudProperty("privatelink"),
            Property.davProperty("getcontentlength"),
            Property.ownCloudProperty("size"),
            Property.davProperty("getlastmodified"), Property.davProperty("getetag"),
            Property.davProperty("getcontenttype"), Property.davProperty("resourcetype"),
            Property.ownCloudProperty("downloadURL")
        )

        assertThat(result).isNotNull()
    }

}