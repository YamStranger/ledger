package com.reut.ledger.rest.handler

import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.Headers.CONTENT_LANGUAGE_STRING
import io.undertow.util.Headers.CONTENT_TYPE_STRING
import io.undertow.util.HttpString
import java.util.UUID

val requestIdKey = AttachmentKey.create(UUID::class.java)

fun HttpServerExchange.addDefaultHeaders() {
    this.responseHeaders
        .put(HttpString(CONTENT_TYPE_STRING), "application/json;charset=UTF-8")
        .put(HttpString(CONTENT_LANGUAGE_STRING), "en")
}
