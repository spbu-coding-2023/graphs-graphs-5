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
        ).firstOrNull()?.let {entity ->
            println(entity.id)
            val graph : Graph<Any> = if (entity.metadata == "true") DirectedGraph() else UndirectedGraph()
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
//    println("Graph not found in database")
        return null
    }
}



//fun main() {
//    val repo = SqliteRepo<Any>("/Users/sofyakozyreva/dddiiieee/mamamiia.db")
//    repo.connectToDatabase()
//    transaction {
//        val graph3 = repo.loadGraphFromDB("ladybird") ?: return@transaction
//        graph3.vertices.forEach {
//            println(it)
//        }
//        graph3.edges.forEach {
//            println(it)
//        }
//    }
//}