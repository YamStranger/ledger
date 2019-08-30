package com.reut.ledger.rest

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

object JsonUtil {
    val gson = GsonBuilder().setPrettyPrinting().create()

    fun toJsonObject(any: Any?): JsonObject = gson.toJsonTree(any).asJsonObject

    fun serialize(any: Any?): String = gson.toJson(toJsonObject(any))

    inline fun <reified T> deserialize(source: String): T {
        val fooType = object : TypeToken<T>() {}.type
        return gson.fromJson(source, fooType)
    }

    fun <T> deserialize(source: String, cl: Class<T>): T {
        return gson.fromJson(source, cl)
    }
}
