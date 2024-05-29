package io

import io.sqlite.*
import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import model.Vertex
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SqliteRepo<V>(pathToDatabase: String) {
    private val database by lazy {
        Database.connect("jdbc:sqlite:$pathToDatabase", driver = "org.sqlite.JDBC")
    }

    fun connectToDatabase() {
        transaction(database) {
            SchemaUtils.create(Graphs, Edges, Vertices)
        }
    }

    fun <V> saveGraphToDB(graph: Graph<V>, name: String) {
        val graphsNamesList = getAllGraphsNames()
        if (graphsNamesList.contains(name)) return
        return transaction {
            val graphId = Graphs.insertAndGetId {
                it[Graphs.name] = name
                it[metadata] = graph.graphType.toString()
            }.value
            val vertexIdMap = mutableMapOf<Int, Int>()
            graph.vertices.forEach { vertex ->
                val vertexId = Vertices.insertAndGetId {
                    it[Vertices.graph] = EntityID(graphId, Graphs)
                    it[data] = vertex.data.toString()
                    it[keyVertexRank] = 0.0
                    it[clusterNum] = 0
                }.value
                // Map the original vertex index to the new database ID
                vertexIdMap[vertex.index] = vertexId
            }
            graph.edges.forEach { edge ->
                val startId = vertexIdMap[edge.source.index] ?: error("start vertex ID not found")
                val endId = vertexIdMap[edge.destination.index] ?: error("end vertex ID not found")
                Edges.insertAndGetId {
                    it[Edges.graph] = EntityID(graphId, Graphs)
                    it[start] = EntityID(startId, Vertices)
                    it[end] = EntityID(endId, Vertices)
                    it[weight] = edge.weight
                }.value
            }
        }
    }

    fun loadGraphFromDB(name: String): Graph<Any>? {
        GraphEntity.find(
            Graphs.name eq name
        ).firstOrNull()?.let { entity ->
            val graph: Graph<Any> = if (entity.metadata == "DIRECTED") DirectedGraph() else UndirectedGraph()
            val vertexIdMap = mutableMapOf<Int, Int>()
            transaction {
                VertexEntity.all().forEach { vertexEntity ->
                    if (vertexEntity.graph == entity) {
                        val vertex = graph.addVertex(vertexEntity.data, vertexEntity.id.toString().toInt())
                        vertexIdMap[vertexEntity.id.toString().toInt()] = vertex.index
                    }
                }
                EdgeEntity.all().forEach { edge ->
                    if (edge.graph == entity) {
                        val startId = vertexIdMap[edge.start.id.toString().toInt()] ?: error("start ID not found")
                        val endId = vertexIdMap[edge.end.id.toString().toInt()] ?: error("end ID not found")
                        graph.addEdge(
                            Vertex(startId, edge.start.data, edge.start.id.toString().toInt()),
                            Vertex(endId, edge.end.data, edge.end.id.toString().toInt()),
                            edge.weight
                        )
                    }
                }
            }
            return graph
        }
        return null
    }

    private fun getAllGraphsNames(): List<String> {
        val graphsNamesList = mutableListOf<String>()
        transaction {
            GraphEntity.all().forEach { entity ->
                graphsNamesList.add(entity.name)
            }
        }
        return graphsNamesList
    }

    fun <V> cleanOutdatedAlgoResults(graph: Graph<V>) {
        val vertices = graph.vertices.toList()
        transaction {
            for (i in vertices.indices) {
                val vertex = vertices[i]
                Vertices.update({ Vertices.id eq vertex.dBIndex }) {
                    it[clusterNum] = 0
                    it[keyVertexRank] = 0.0
                }
            }
        }
    }

    fun <V> saveKeyVerticesResults(graph: Graph<V>, rankingList: List<Double>) {
        val vertices = graph.vertices.toList()
        transaction {
            for (i in rankingList.indices) {
                val vertex = vertices[i]
                val vertexRank = rankingList[i]
                Vertices.update({ Vertices.id eq vertex.dBIndex }) {
                    it[keyVertexRank] = vertexRank
                }
            }
        }
    }

    fun <V> saveClusterDetectionResults(graph: Graph<V>, clusterPartition: IntArray) {
        val vertices = graph.vertices.toList()
        transaction {
            for (i in clusterPartition.indices) {
                val vertex = vertices[i]
                val clusterNumber = clusterPartition[i]
                Vertices.update({ Vertices.id eq vertex.dBIndex }) {
                    it[clusterNum] = clusterNumber
                }
            }
        }
    }

    fun loadKeyVerticesResults(name: String): MutableList<Double>? {
        GraphEntity.find(
            Graphs.name eq name
        ).firstOrNull()?.let { entity ->
            val graph = loadGraphFromDB(name) ?: return null
            val rankingList = MutableList(graph.vertices.size) { 0.0 }
            transaction {
                VertexEntity.all().forEach { vertexEntity ->
                    if (vertexEntity.graph == entity) {
                        rankingList.add(vertexEntity.id.toString().toInt(), vertexEntity.keyVertexRank)
                    }
                }
            }
            val newList = mutableListOf<Double>()
            graph.vertices.forEach { v ->
                newList.add(rankingList[v.dBIndex])
            }
            return newList
        }
        return null
    }

    fun loadClusteringResults(name: String): List<Int>? {
        GraphEntity.find(
            Graphs.name eq name
        ).firstOrNull()?.let { entity ->
            val graph = loadGraphFromDB(name) ?: return null
            val clusterNumList = MutableList(graph.vertices.size) { 0 }
            transaction {
                VertexEntity.all().forEach { vertexEntity ->
                    if (vertexEntity.graph == entity) {
                        clusterNumList.add(vertexEntity.id.toString().toInt(), vertexEntity.clusterNum)
                    }
                }
            }
            val newList = mutableListOf<Int>()
            graph.vertices.forEach { v ->
                newList.add(clusterNumList[v.dBIndex])
            }
            return newList
        }
        return null
    }
}