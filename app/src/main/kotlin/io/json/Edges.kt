package io.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SerializableEdge<V>(
    val index: Int,
    val source: Int,
    val destination: Int,
    val weight: Double
)