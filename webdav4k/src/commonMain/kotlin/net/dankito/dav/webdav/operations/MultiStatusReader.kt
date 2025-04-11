package net.dankito.dav.webdav.operations

import net.codinux.log.logger
import net.dankito.dav.serialization.*
import net.dankito.dav.webdav.model.*
import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.allText
import nl.adaptivity.xmlutil.xmlStreaming

open class MultiStatusReader {

    companion object {
        val Instance = MultiStatusReader()
    }


    protected val log by logger()


    open fun parse(xml: String): MultiStatus? = try {
        val reader = xmlStreaming.newReader(fixXmlForReading(xml))

        if (reader.readToNextStartTag() == false || reader.localName != "multistatus") {
            null
        } else {
            val responses = mutableListOf<Response>()
            var responseDescription: String? = null
            var syncToken: String? = null

            while (reader.eventType != EventType.END_DOCUMENT && reader.readToNextStartTag()) {
                when (reader.localName) {
                    "response" -> readResponse(reader)?.let { responses.add(it) }
                    "responsedescription" -> responseDescription = readText(reader)
                    "syncToken" -> syncToken = readText(reader)
                }
            }

            MultiStatus(responses, responseDescription, syncToken)
        }
    } catch (e: Throwable) {
        log.error(e) { "Could not parse XML to MultiStatus object:\n$xml" }
        null
    }


    protected open fun readResponse(reader: XmlReader): Response? = try {
        val href = mutableListOf<String>()
        val propStats = mutableListOf<PropStat>()
        var status: Status? = null
        var responseDescription: String? = null
        var location: String? = null
        var error: Error? = null

        while (reader.nextIsNotEndDocument() && reader.isStillInElement("response")) {
            if (reader.isStartElement()) {
                when (reader.localName) {
                    "href" -> readText(reader)?.let { href.add(it) }
                    "propstat" -> readPropStat(reader)?.let { propStats.add(it) }
                    "status" -> status = readStatus(reader)
                    "responsedescription" -> responseDescription = readText(reader)
                    "location" -> location = readText(reader)
                    "error" -> error = readError(reader)
                }
            }
        }

        Response(href, propStats, status, responseDescription, location, error)
    } catch (e: Throwable) {
        log.error(e) { "Could not read MultiStatus' Response element" }
        null
    }

    protected open fun readPropStat(reader: XmlReader): PropStat? = try {
        var properties = emptyList<Property>()
        var status: Status? = null
        var responseDescription: String? = null
        var error: Error? = null

        while (reader.nextIsNotEndDocument() && reader.isStillInElement("propstat")) {
            if (reader.isStartElement()) {
                when (reader.localName) {
                    "prop" -> properties = readProp(reader)
                    "status" -> status = readStatus(reader)
                    "responsedescription" -> responseDescription = readText(reader)
                    "error" -> error = readError(reader)
                }
            }
        }

        PropStat(properties, status, responseDescription, error)
    } catch (e: Throwable) {
        log.error(e) { "Could not read MultiStatus Response's PropStat element" }
        null
    }

    protected open fun readProp(reader: XmlReader): List<Property> {
        val properties = mutableListOf<Property>()

        while (reader.nextIsNotEndDocument() && reader.isStillInElement("prop")) {
            if (reader.isStartElement()) {
                readProperty(reader)?.let { properties.add(it) }
            }
        }

        return properties
    }

    protected open fun readProperty(reader: XmlReader): Property? = try {
        val name = reader.localName
        val namespaceUri = reader.namespaceURI
        val prefix = reader.prefix

        when (reader.next()) {
            EventType.TEXT -> Property(name, namespaceUri, prefix, readText(reader))
            EventType.START_ELEMENT -> Property(name, namespaceUri, prefix, null, readPropertyChildren(name, reader))
            else -> Property(name, namespaceUri, prefix)
        }
    } catch (e: Throwable) {
        log.error(e) { "Could not read MultiStatus Response PropStat's Property element" }
        null
    }

    // e.g. dav:resourcetype can have child elements, see e.g. https://github-wiki-see.page/m/dmfs/davwiki/wiki/DAV%3A%3Aresourcetype
    protected open fun readPropertyChildren(parentPropertyName: String, reader: XmlReader): List<Property> {
        val children = mutableListOf<Property>()

        readProperty(reader)?.let { children.add(it) }

        while (reader.nextIsNotEndElementOf(parentPropertyName)) {
            if (reader.isStartElement()) {
                readProperty(reader)?.let { children.add(it) }
            }
        }

        return children
    }


    protected open fun readStatus(reader: XmlReader): Status? = try {
        val statusLine = readText(reader)

        if (statusLine == null) {
            null
        } else {
            // the status line has this format:
            // HTTP/<version> <status-code> <reason-phrase>
            val parts = statusLine.split(' ', limit = 3).map { it.trim() }
            if (parts.size != 3) { // cannot parse statusLine then
                Status(statusLine)
            } else {
                val httpVersion = if (parts[0].startsWith("HTTP/", true)) parts[0].substring(4) else null
                val httpStatusCode = parts[1].toIntOrNull()
                Status(statusLine, httpVersion, httpStatusCode, parts[2])
            }
        }
    } catch (e: Throwable) {
        log.error(e) { "Could not read Status" }
        null
    }

    protected open fun readError(reader: XmlReader) = Error(reader.allText())

    protected open fun readText(reader: XmlReader): String? =
        if (reader.eventType == EventType.TEXT) {
            reader.text
        } else if (reader.nextIsText()) {
            reader.text
        } else {
            null
        }

}