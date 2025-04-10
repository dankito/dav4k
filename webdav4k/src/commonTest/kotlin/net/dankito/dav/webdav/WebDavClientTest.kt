package net.dankito.dav.webdav

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isIn
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import kotlinx.coroutines.test.runTest
import net.dankito.dav.webdav.model.Property
import net.dankito.dav.webdav.options.UploadFileOptions
import kotlin.test.Test

class WebDavClientTest {

    private val underTest = WebDavClient(TestCredentials.createWebClient())

    private val localWebDavClient = WebDavClient("http://localhost:6065")


    @Test
    fun list_DefaultProperties() = runTest {
        val result = underTest.list("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe", 1)

        assertThat(result).isNotNull()
        assertThat(result!!.responses).hasSize(3)

        result.responses.forEach { response ->
            assertThat(response.href).hasSize(1)
            assertThat(response.propStats).hasSize(2)

            val successResponse = response.propStats.first { it.status?.isSuccess == true }
            assertThat(successResponse).isNotNull()
            assertThat(successResponse.properties.size).isIn(13, 15)

            // some properties don't exist
            val notFoundResponse = response.propStats.first { it.status?.httpStatusCode == 404 }
            assertThat(notFoundResponse).isNotNull()
            assertThat(notFoundResponse.properties.size).isIn(4)
        }
    }

    @Test
    fun list_SpecifyWhichPropertiesToReturn() = runTest {
        val result = underTest.list("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe", 1,
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
        assertThat(result!!.responses).hasSize(3)

        result.responses.forEach { response ->
            assertThat(response.href).hasSize(1)
            assertThat(response.propStats).hasSize(2)

            val successResponse = response.propStats.first { it.status?.isSuccess == true }
            assertThat(successResponse).isNotNull()
            assertThat(successResponse.properties.size).isIn(10, 12)

            // some properties don't exist
            val notFoundResponse = response.propStats.first { it.status?.httpStatusCode == 404 }
            assertThat(notFoundResponse).isNotNull()
            assertThat(notFoundResponse.properties.size).isIn(2, 4)
        }
    }


    @Test
    fun uploadFile() = runTest {
        val fileUrl = "/test.txt"
        val fileContent = "Hallo Stasi,\nich liebe dich!"

        val result = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(result).isTrue()


        val filesOnServer = localWebDavClient.list(fileUrl)

        assertThat(filesOnServer).isNotNull()
        assertThat(filesOnServer!!.responses).hasSize(1)
        // TODO: store test start time and check if lastUpdateTime is after test start time
    }

    @Test
    fun localWebDav() = runTest {
        val result = localWebDavClient.list("/")

        assertThat(result).isNotNull()
    }

}