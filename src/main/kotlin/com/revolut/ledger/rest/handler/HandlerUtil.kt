package com.revolut.ledger.rest.handler

import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.util.HttpString

fun HttpServerExchange.addDefaultHeaders() {
    this.responseHeaders
        .put(HttpString(Headers.CONTENT_TYPE_STRING), "application/json;charset=UTF-8")
        .put(HttpString(Headers.CONTENT_LANGUAGE_STRING), "en")
}
