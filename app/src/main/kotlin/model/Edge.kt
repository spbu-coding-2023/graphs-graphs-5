package model

data class Edge<V>(
    val index: Int,
    val source: Vertex<V>,
    val destination: Vertex<V>,
    val weight: Double = 1.0
)