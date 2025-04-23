package net.dankito.dav.webdav

import assertk.assertThat
import assertk.assertions.*
import kotlinx.coroutines.test.runTest
import net.dankito.dav.DefaultNamespaces
import net.dankito.dav.Success
import net.dankito.dav.webdav.model.DavResource
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

        assertThat(result.isSuccess()).isTrue()
        val filesAndFolders = (result as Success).data
        assertThat(filesAndFolders).hasSize(3)

        val files = filesAndFolders.filter { it.isFile }
        val folders = filesAndFolders.filter { it.isFolder }
        assertThat(files).hasSize(1)
        assertThat(folders).hasSize(2)

        filesAndFolders.forEach { resource ->
            assertThat(resource.url).isNotEmpty()
            assertThat(resource.properties.size).isIn(13, 15)
            resource.properties.forEach { property ->
                // either direct value or children must be set
                if ((property.name == "resourcetype" && property.isDavProperty) == false &&
                    (property.name == "tags" && property.namespaceUri == DefaultNamespaces.OwnCloud) == false) { // ok, ownCloud's tags property may be empty
                    assertThat(property.value != null || property.children.isNotEmpty()).isTrue()
                }
            }

            // some properties don't exist
            assertThat(resource.notFoundProperties).hasSize(4)
            resource.notFoundProperties.forEach { property ->
                // values or children may not be set then
                assertThat(property.value).isNull()
                assertThat(property.children).isEmpty()
            }

            // asserting property values have been mapped
            assertThat(resource.displayname).isNotNull().isNotEmpty()
            assertThat(resource.lastModified).isNotNull()
            assertThat(resource.lastModified!!.epochSeconds).isGreaterThan(1_744_000_000)
            assertThat(resource.etag).isNotNull().isNotEmpty()
            if (resource.isFile) {
                assertThat(resource.contentType).isNotNull().isNotEmpty()
                assertThat(resource.contentLength).isNotNull().isGreaterThan(0)
            }

            // assert ownCloud properties
            assertThat(resource.findOwnCloudPropertyValue("permissions")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("favorite")?.toIntOrNull()).isNotNull().isEqualTo(0)
            assertThat(resource.findOwnCloudPropertyValue("fileid")).isNotNull().isNotEmpty()
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

        assertThat(result.isSuccess()).isTrue()
        val filesAndFolders = (result as Success).data
        assertThat(filesAndFolders).hasSize(3)

        val files = filesAndFolders.filter { it.isFile }
        val folders = filesAndFolders.filter { it.isFolder }
        assertThat(files).hasSize(1)
        assertThat(folders).hasSize(2)

        filesAndFolders.forEach { resource ->
            assertThat(resource.url).isNotEmpty()

            if (resource.isFolder) {
                assertThat(resource.properties).hasSize(10)

                // some properties don't exist
                assertThat(resource.notFoundProperties).hasSize(4)

                // properties not existing on folders
                assertThat(resource.notFoundProperties.map { it.name }).containsAtLeast("getcontenttype", "getcontentlength")
            } else if (resource.isFile) {
                assertThat(resource.properties).hasSize(12)
                assertThat(resource.notFoundProperties).hasSize(2)
            }

            // properties that don't exist neither on files nor on folders
            assertThat(resource.notFoundProperties.map { it.name }).containsAtLeast("share-types", "downloadURL")

            // asserting property values have been mapped
            assertThat(resource.lastModified).isNotNull()
            assertThat(resource.lastModified!!.epochSeconds).isGreaterThan(1_745_000_000)
            assertThat(resource.etag).isNotNull().isNotEmpty()
            if (resource.isFile) {
                assertThat(resource.contentType).isNotNull().isNotEmpty()
                assertThat(resource.contentLength).isNotNull().isGreaterThan(0)
            }

            // assert ownCloud properties
            assertThat(resource.findOwnCloudPropertyValue("permissions")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("favorite")?.toIntOrNull()).isNotNull().isEqualTo(0)
            assertThat(resource.findOwnCloudPropertyValue("fileid")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("owner-id")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("owner-display-name")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("privatelink")).isNotNull().isNotEmpty()
            assertThat(resource.findOwnCloudPropertyValue("size")?.toIntOrNull()).isNotNull().isGreaterThanOrEqualTo(0)
        }
    }


    @Test
    fun createDirectory() = runTest {
        val directoryUrl = "/testSubDirectory"

        localWebDavClient.deleteFileOrDirectory(directoryUrl) // ensure directory does not already exist


        val result = localWebDavClient.createDirectory(directoryUrl)

        assertThat(result.isSuccess()).isTrue()


        localWebDavClient.deleteFileOrDirectory(directoryUrl)
    }

    @Test
    fun downloadFile() = runTest {
        val uploadResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadResult.isSuccess()).isTrue()


        val result = localWebDavClient.downloadFile(fileUrl)

        assertThat(result.isSuccess()).isTrue()
        assertThat((result as Success).data).hasSize(fileContent.length)
        assertThat(result.data.decodeToString()).isEqualTo(fileContent)

        localWebDavClient.deleteFileOrDirectory(fileUrl)
    }

    @Test
    fun uploadFile() = runTest {
        val result = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(result.isSuccess()).isTrue()


        val filesOnServer = localWebDavClient.listResource(fileUrl)

        assertThat(filesOnServer.isSuccess()).isTrue()
        // TODO: store test start time and check if lastUpdateTime is after test start time

        localWebDavClient.deleteFileOrDirectory(fileUrl)
    }


    @Test
    fun deleteFile() = runTest {
        val uploadResult = localWebDavClient.uploadFile(fileUrl, "Any content".encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadResult.isSuccess()).isTrue()

        val filesOnServerBefore = localWebDavClient.listResource(fileUrl)

        assertThat(filesOnServerBefore.isSuccess()).isTrue()


        val result = localWebDavClient.deleteFileOrDirectory(fileUrl)


        assertThat(result.isSuccess()).isTrue()

        assertIsDeleted(fileUrl)
    }

    @Test
    fun copyFile() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult.isSuccess()).isTrue()


        val result = localWebDavClient.copyFile(fileUrl, destinationFileUrl, true)

        assertThat(result.isSuccess()).isTrue()


        localWebDavClient.deleteFileOrDirectory(fileUrl)

        localWebDavClient.deleteFileOrDirectory(destinationFileUrl)
    }

    @Test
    fun moveFile() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult.isSuccess()).isTrue()


        val result = localWebDavClient.moveFile(fileUrl, destinationFileUrl, true)

        assertThat(result.isSuccess()).isTrue()


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

        assertThat(uploadFileResult.isSuccess()).isTrue()


        val result = localWebDavClient.moveFile(sourceDirectory, destinationDirectory + sourceDirectory, true)

        assertThat(result.isSuccess()).isTrue()


        val destinationDirContentResult = localWebDavClient.list(destinationDirectory, Depth.Infinity)
        assertThat(destinationDirContentResult.isSuccess()).isTrue()
        val destinationDirContent = (destinationDirContentResult as Success).data
        assertThat(destinationDirContent).hasSize(3) // assert that also containing file has been moved

        val files = destinationDirContent.filter { it.isFile }
        val folders = destinationDirContent.filter { it.isFolder }
        assertThat(files).hasSize(1)
        assertThat(folders).hasSize(2)


        localWebDavClient.deleteFileOrDirectory(destinationDirectory)

        assertIsDeleted(destinationDirectory)
        assertIsDeleted(sourceDirectory)
        assertIsDeleted(sourceDirectory + fileUrl)
    }

    @Test
    fun fileExists() = runTest {
        val uploadFileResult = localWebDavClient.uploadFile(fileUrl, fileContent.encodeToByteArray(), UploadFileOptions("text/plain", true))

        assertThat(uploadFileResult.isSuccess()).isTrue()


        val result = localWebDavClient.fileExists(fileUrl)

        assertThat(result.isSuccess()).isTrue()


        localWebDavClient.deleteFileOrDirectory(destinationFileUrl)
    }

    @Test
    fun fileDoesNotExist() = runTest {
        val fileUrl = "/i_do_not_exist.txt"

        localWebDavClient.deleteFileOrDirectory(fileUrl)


        val result = localWebDavClient.fileExists(fileUrl)

        assertThat(result.isSuccess()).isFalse()
        assertThat(result.isFailure()).isTrue()
    }

    @Test
    fun getAvailablePropertyNames() = runTest {
        val result = localWebDavClient.getAvailablePropertyNames("/", Depth.DirectoryListing)

        assertThat(result.isSuccess()).isTrue()
        assertThat((result as Success).data).hasSize(3)
    }

    @Test
    fun localWebDav() = runTest {
        val result = localWebDavClient.listResource("/")

        assertThat(result.isSuccess()).isTrue()
    }


    private suspend fun assertIsDeleted(fileUrl: String) {
        val fileOnServer = localWebDavClient.listResource(fileUrl)

        if (fileOnServer.isFailure()) {
            assertThat(fileOnServer.response.statusCode).isEqualTo(404)
        } else if (fileOnServer.isSuccess()) {
            // hm, the WebDAV server i use still returns the file, which is not conformant to standard, it just sets its contentlength to 0
            assertThat((fileOnServer as Success<DavResource, String>).data.contentLength).isEqualTo(0) // why doesn't smart cast to Success<DavResource, String> work?
        }
    }


    companion object {
        private const val fileUrl = "/test.txt"

        private const val destinationFileUrl = "/copy.txt"

        private const val fileContent = "Hallo Stasi,\nich liebe dich!"
    }

}