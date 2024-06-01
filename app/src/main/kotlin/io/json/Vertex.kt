package io.json

import kotlinx.serialization.Serializable

@Serializable
data class SerializableVertex<V>(
    val index: Int,
    val data: V,
    val dBIndex: Int
)