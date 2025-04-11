package net.dankito.dav.webdav

import assertk.assertThat
import assertk.assertions.*
import kotlinx.coroutines.test.runTest
import net.dankito.dav.DefaultNamespaces
import net.dankito.dav.webdav.model.Depth
import net.dankito.dav.webdav.model.Property
import net.dankito.dav.webdav.options.UploadFileOptions
import kotlin.test.Test

class WebDavClientTest {

    private val underTest = WebDavClient(TestCredentials.createWebClient())

    private val localWebDavClient = WebDavClient("http://localhost:6065")


    @Test
    fun list_DefaultProperties() = runTest {
        val result = underTest.listDirectory("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe")

        assertThat(result).hasSize(3)

        result.forEach { resource ->
            assertThat(resource.url).isNotEmpty()
            assertThat(resource.properties.size).isIn(13, 15)
            resource.properties.forEach { property ->
                // either direct value or children must be set
                if (property.name != "tags" && property.namespaceUri != DefaultNamespaces.OwnCloud) { // ok, ownCloud's tags property may be empty
                    assertThat(property.value != null || property.children.isNotEmpty()).isTrue()
                }
            }

            // some properties don't exist
            assertThat(resource.notFoundProperties.size).isIn(4)
            resource.notFoundProperties.forEach { property ->
                // values or children may not be set then
                assertThat(property.value).isNull()
                assertThat(property.children).isEmpty()
            }
        }
    }

    @Test
    fun list_SpecifyWhichPropertiesToReturn() = runTest {
        val result = underTest.listDirectory("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe",
            // builds this ownCloud example: https://owncloud.dev/apis/http/webdav/#listing-properties
            Property.ownCloudProperty("permissions"), Property.ownCloudProperty("favorite"),
            Property.ownCloudProperty("fileid"),
            Property.ownCloudProperty("owner-id"), Property.ownCloudProperty("owner-display-name"),
            Property.ownCloudProperty("share-types"), Property.ownCloudProperty("privatelink"),
            Property.Dav.ContentLength,
            Property.ownCloudProperty("size"),
            Property.Dav.LastModified, Property.Dav.ETag,
            Property.Dav.ContentType, Property.Dav.ResourceType,
            Property.ownCloudProperty("downloadURL")
        )

        assertThat(result).hasSize(3)

        result.forEach { resource ->
            assertThat(resource.url).isNotEmpty()

            assertThat(resource.properties.size).isIn(10, 12)

            // some properties don't exist
            assertThat(resource.notFoundProperties.size).isIn(2, 4)
        }
    }


    @Test
    fun createDirectory() = runTest {
        val directoryUrl = "/testSubDirectory"

        localWebDavClient.deleteFileOrDirectory(directoryUrl) // ensure directory does not already exist


        val result = localWebDavClient.createDirectory(directoryUrl)

        assertThat(result).isTrue()


        localWebDavClient.deleteFileOrDirectory(directoryUrl)
    }

    @Test
    fun downloadFile() = runTest {
        val uploadResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadResult).isTrue()


        val result = localWebDavClient.downloadFile(fileUrl)

        assertThat(result).isNotNull().hasSize(fileContent.length)
        assertThat(result!!.decodeToString()).isEqualTo(fileContent)

        localWebDavClient.deleteFileOrDirectory(fileUrl)
    }

    @Test
    fun uploadFile() = runTest {
        val result = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(result).isTrue()


        val filesOnServer = localWebDavClient.listResource(fileUrl)

        assertThat(filesOnServer).isNotNull()
        // TODO: store test start time and check if lastUpdateTime is after test start time

        localWebDavClient.deleteFileOrDirectory(fileUrl)
    }


    @Test
    fun deleteFile() = runTest {
        val uploadResult = localWebDavClient.uploadFile(fileUrl, "Any content".encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadResult).isTrue()

        val filesOnServerBefore = localWebDavClient.listResource(fileUrl)

        assertThat(filesOnServerBefore).isNotNull()


        val result = localWebDavClient.deleteFileOrDirectory(fileUrl)


        assertThat(result).isTrue()

        assertIsDeleted(fileUrl)
    }

    @Test
    fun copyFile() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult).isTrue()


        val result = localWebDavClient.copyFile(fileUrl, destinationFileUrl, true)

        assertThat(result).isTrue()


        localWebDavClient.deleteFileOrDirectory(fileUrl)

        localWebDavClient.deleteFileOrDirectory(destinationFileUrl)
    }

    @Test
    fun moveFile() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult).isTrue()


        val result = localWebDavClient.moveFile(fileUrl, destinationFileUrl, true)

        assertThat(result).isTrue()


        localWebDavClient.deleteFileOrDirectory(destinationFileUrl)

        assertIsDeleted(fileUrl)
    }

    @Test
    fun moveDirectory() = runTest {
        val sourceDirectory = "/sourceDir"
        val destinationDirectory = "/destinationDir"
        localWebDavClient.createDirectory(sourceDirectory)
        localWebDavClient.createDirectory(destinationDirectory)

        val uploadFileResult = localWebDavClient.uploadFile(sourceDirectory + fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult).isTrue()


        val result = localWebDavClient.moveFile(sourceDirectory, destinationDirectory + sourceDirectory, true)

        assertThat(result).isTrue()


        val destinationDirContent = localWebDavClient.list(destinationDirectory, Depth.Infinity)
        assertThat(destinationDirContent).hasSize(3) // assert that also containing file has been moved


        localWebDavClient.deleteFileOrDirectory(destinationDirectory)

        assertIsDeleted(destinationDirectory)
        assertIsDeleted(sourceDirectory)
        assertIsDeleted(sourceDirectory + fileUrl)
    }

    @Test
    fun fileExists() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult).isTrue()


        val result = localWebDavClient.fileExists(fileUrl)

        assertThat(result).isTrue()


        localWebDavClient.deleteFileOrDirectory(destinationFileUrl)
    }

    @Test
    fun fileDoesNotExist() = runTest {
        val fileUrl = "/i_do_not_exist.txt"

        localWebDavClient.deleteFileOrDirectory(fileUrl)


        val result = localWebDavClient.fileExists(fileUrl)

        assertThat(result).isFalse()
    }

    @Test
    fun getAvailablePropertyNames() = runTest {
        val result = localWebDavClient.getAvailablePropertyNames("/", Depth.DirectoryListing)

        assertThat(result).hasSize(3)
    }

    @Test
    fun localWebDav() = runTest {
        val result = localWebDavClient.listResource("/")

        assertThat(result).isNotNull()
    }


    private suspend fun assertIsDeleted(fileUrl: String) {
        val fileOnServer = localWebDavClient.listResource(fileUrl)

        if (fileOnServer == null) {
            assertThat(fileOnServer).isNull()
        } else {
            // hm, the WebDAV server i use still returns the file, which is not conformant to standard, it just sets its contentlength to 0
            assertThat(fileOnServer.properties.first { it.name == "getcontentlength" }.value).isEqualTo("0")
        }
    }


    companion object {
        private const val fileUrl = "/test.txt"

        private const val destinationFileUrl = "/copy.txt"

        private const val fileContent = "Hallo Stasi,\nich liebe dich!"
    }

}