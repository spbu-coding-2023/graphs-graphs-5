package io.json

import io.json.SerializableEdge
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.*
import java.io.File

//fun <V> Graph<V>.toSerializableGraph(): SerializableGraph<V> {
//    val vertices = this.vertices.map { vertex ->
//        SerializableVertex(vertex.index, vertex.data, vertex.dBIndex)
//    }
//
//    val edges = this.edges.map { edge ->
//        SerializableEdge<V>(edge.index, edge.source.index, edge.destination.index, edge.weight)
//    }
//
//    return SerializableGraph(vertices, edges, this.graphType)
//}

fun <V> Graph<V>.saveToJsonFile(filePath: String) {
    val serializableGraph = this.toSerializableGraph()
    val json = Json { prettyPrint = true }
    val jsonString = json.encodeToString(serializableGraph)
    File(filePath).writeText(jsonString)
}

fun <V> Graph<V>.toSerializableGraph(): SerializableGraph<V> {
    val vertices = this.vertices.map { vertex ->
        SerializableVertex(vertex.index, vertex.data, vertex.dBIndex)
    }

    val edges = this.edges.map { edge ->
        SerializableEdge<V>(edge.index, edge.source.index, edge.destination.index, edge.weight)
    }

    return SerializableGraph(vertices, edges, this.graphType)
}


//fun <V> fromSerializableGraph(serializableGraph: SerializableGraph<V>, createVertex: (SerializableVertex<V>) -> Vertex<V>): Graph<V> {
//    val graph: Graph<V> = when (serializableGraph.graphType) {
//        GraphType.DIRECTED -> DirectedGraph()
//        GraphType.UNDIRECTED -> UndirectedGraph()
//    }
//
//    val vertexMap = mutableMapOf<Int, Vertex<V>>()
//
//    // Create vertices
//    for (vertex in serializableGraph.vertices) {
//        val newVertex = createVertex(vertex)
//        graph.addVertex(newVertex.data, newVertex.dBIndex)
//        vertexMap[vertex.index] = newVertex
//    }
//
//    // Create edges
//    for (edge in serializableGraph.edges) {
//        val sourceVertex = vertexMap[edge.source]
//        val destinationVertex = vertexMap[edge.destination]
//        if (sourceVertex != null && destinationVertex != null) {
//            graph.addEdge(sourceVertex, destinationVertex, edge.weight)
//        }
//    }
//
//    return graph
//}

//fun <V> loadFromJsonFile(filePath: String, createVertex: (SerializableVertex<V>) -> Vertex<V>): Graph<V> {
//    val jsonString = File(filePath).readText()
//    val json = Json { prettyPrint = true }
//    val serializableGraph = json.decodeFromString<SerializableGraph<V>>(jsonString)
//    return fromSerializableGraph(serializableGraph, createVertex)
//}