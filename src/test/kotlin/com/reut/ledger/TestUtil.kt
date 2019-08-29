package com.reut.ledger

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.reut.ledger.rest.JsonUtil
import java.io.IOException
import java.net.ServerSocket

inline fun <reified T : Any> Triple<Request, Response, Result<T, FuelError>>.extractErrorIfExists(): Triple<Response, T?, FuelError?> {
    val error = this.third.component2()
    val parsedBody = this.third.component1() ?: let {
        val data = this.second.data
        if (data.isNotEmpty()) {
            JsonUtil.deserialize<T>(String(data))
        } else null
    }

    return Triple(this.second, parsedBody, error)
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
