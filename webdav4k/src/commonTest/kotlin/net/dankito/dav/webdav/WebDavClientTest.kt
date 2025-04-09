package net.dankito.dav.webdav

import assertk.assertThat
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class WebDavClientTest {

    private val underTest = WebDavClient(TestCredentials.createWebClient())


    @Test
    fun list() = runTest {
        val result = underTest.list("spaces/cb7e504b-d254-4643-9f5b-c51ad4743938\$6f2ce006-3ee0-41c2-b1d4-b35e6d3a0efe")

        assertThat(result).isNotNull()
    }

}