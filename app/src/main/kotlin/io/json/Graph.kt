package io.json

import kotlinx.serialization.Serializable
import model.GraphType


@Serializable
data class SerializableGraph<V>(
    val vertices: List<SerializableVertex<V>>,
    val edges: List<SerializableEdge<V>>,
    val graphType: GraphType
)
