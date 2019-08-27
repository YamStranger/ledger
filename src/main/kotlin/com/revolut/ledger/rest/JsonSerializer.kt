package com.revolut.ledger.rest

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object JsonSerializer {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    fun toJson(any: Any?): JsonObject = gson.toJsonTree(any).asJsonObject

    fun serialize(any: JsonObject): String = gson.toJson(any)
}
