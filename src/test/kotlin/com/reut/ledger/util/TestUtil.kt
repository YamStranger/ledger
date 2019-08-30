package com.reut.ledger.util

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.reut.ledger.rest.util.JsonUtil
import java.io.IOException
import java.net.ServerSocket

inline fun <reified T : Any> Request.execute(): Triple<Response, T?, FuelError?> {
    val (_, response, result) = this.responseObject<T>()
    val error = result.component2()
    val parsedBody = result.component1() ?: let {
        val data = response.data
        if (data.isNotEmpty()) {
            JsonUtil.deserialize<T>(String(data))
        } else null
    }
    return Triple(response, parsedBody, error)
}

fun findFreePort(): Int {
    var socket: ServerSocket? = null
    try {
        socket = ServerSocket(0)
        socket.reuseAddress = true
        val port = socket.localPort
        try {
            socket.close()
        } catch (e: IOException) {
            // Ignore IOException on close()
        }
        return port
    } catch (e: IOException) {
    } finally {
        if (socket != null) {
            try {
                socket.close()
            } catch (e: IOException) {
            }
        }
    }
    throw IllegalStateException("Could not find a free TCP/IP port to start embedded HTTP Server on")
}
