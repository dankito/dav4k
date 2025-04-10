package net.dankito.dav.serialization

import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlReader

/**
 * XmlReader crashes if XML e.g. starts with a non-breaking space. So remove everything before first '<'
 */
fun fixXmlForReading(xml: String): String =
    xml.indexOf('<').let { index ->
        if (index < 1) xml
        else xml.substring(index)
    }


val XmlReader.isNotStartElement: Boolean
    get() = this.isStartElement() == false

val XmlReader.isNotEndElement: Boolean
    get() = this.isEndElement() == false && this.eventType != EventType.END_DOCUMENT


fun XmlReader.readToNextStartTag(): Boolean =
    readToNext(EventType.START_ELEMENT)

fun XmlReader.readToNext(type: EventType): Boolean {
    while (hasNext()) { // .nextTag() throws an exception on TEXT events
        val event = this.next()
        if (event == type) {
            return true
        }
    }

    return false
}

fun XmlReader.nextIsText(): Boolean =
    isNext(EventType.TEXT)

fun XmlReader.isNext(type: EventType): Boolean {
    while (hasNext()) { // .nextTag() throws an exception on TEXT events
        val event = this.next()
        return event == type
    }

    return false
}

fun XmlReader.nextIsNotEndDocument(): Boolean =
    nextIsNot(EventType.END_DOCUMENT)

fun XmlReader.nextIsNot(type: EventType): Boolean {
    while (hasNext()) { // .nextTag() throws an exception on TEXT events
        val event = this.next()
        return event != type
    }

    return false
}

fun XmlReader.isStillInElement(tagName: String): Boolean =
    this.hasNext() && (this.isNotEndElement || this.localName != tagName)