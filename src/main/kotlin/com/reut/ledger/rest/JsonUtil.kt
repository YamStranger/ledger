package com.reut.ledger.rest

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

object JsonUtil {
    val gson = GsonBuilder().setPrettyPrinting().create()

    fun toJson(any: Any?): JsonObject = gson.toJsonTree(any).asJsonObject

    fun serialize(any: JsonObject): String = gson.toJson(any)

    inline fun <reified T> deserialize(source: String): T {
        val fooType = object : TypeToken<T>() {}.type
        return gson.fromJson(source, fooType)
    }
}
