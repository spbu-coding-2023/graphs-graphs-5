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
//            println(entity.metadata)
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
//    println("Graph not found in database")
        return null
    }

    private fun getAllGraphsNames(): List<String> {
        val graphsNamesList = mutableListOf<String>()
        transaction {
            GraphEntity.all().forEach{ entity ->
                graphsNamesList.add(entity.name)
            }
        }
        return graphsNamesList
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

    fun loadKeyVerticesResults(name: String) : MutableList<Double>? {
        GraphEntity.find(
            Graphs.name eq name
        ).firstOrNull()?.let {entity ->
            val graph = loadGraphFromDB(name) ?: return null
            val rankingList = MutableList(graph.vertices.size) { 0.0 }
//            val rankingList = mutableListOf<Double>()
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
        ).firstOrNull()?.let {entity ->
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


fun main() {
    val repo = SqliteRepo<Any>("/Users/sofyakozyreva/dddiiieee/new.db")
    repo.connectToDatabase()
    transaction {
        val graph = DirectedGraph<Int>()

    val  Alabama = graph.addVertex(1)
    val  Arizona = graph.addVertex(2)
    val  California = graph.addVertex(3)
    val  Connecticut = graph.addVertex(4)
    val  Florida = graph.addVertex(5)
    val  Hawaii = graph.addVertex(6)
    val  Illinois = graph.addVertex(7)
    val  Iowa = graph.addVertex(8)
    val  Kentucky = graph.addVertex(9)
    val  Maine = graph.addVertex(10)
    val  Massachusetts = graph.addVertex(11)
    val  Minnesota = graph.addVertex(12)
    val  Missouri = graph.addVertex(13)
    val  Montana = graph.addVertex(14)
    val  Nevada = graph.addVertex(15)
    val  NewJersey = graph.addVertex(16)
    val  NewYork = graph.addVertex(17)

    graph.addEdge(Alabama, Illinois)
    graph.addEdge(Alabama, Connecticut, -5.0)
    graph.addEdge(Alabama, Florida)
    graph.addEdge(Alabama, Hawaii)
    graph.addEdge(Alabama, Kentucky)
    graph.addEdge(Kentucky, Iowa)
    graph.addEdge(Iowa, Alabama)
    graph.addEdge(Kentucky, Montana)
    graph.addEdge(Kentucky, California)
    graph.addEdge(Kentucky, Maine)
    graph.addEdge(Kentucky, NewJersey)
    graph.addEdge(Missouri, Montana)
    graph.addEdge(Montana, NewJersey)
    graph.addEdge(California, Massachusetts)
    graph.addEdge(California, Minnesota)
    graph.addEdge(California, Maine)
    graph.addEdge(California, Montana)
    graph.addEdge(Iowa, Arizona)
    graph.addEdge(Iowa, Montana)
    graph.addEdge(Iowa, NewJersey)
    graph.addEdge(Arizona, Montana)
    graph.addEdge(Arizona, Nevada)
    graph.addEdge(Arizona, NewJersey)
    graph.addEdge(Arizona, NewYork)
    graph.addEdge(Montana, Nevada)
        repo.saveGraphToDB(graph, "pipi")
    }
    transaction {
        val graph3 = repo.loadGraphFromDB("pipi") ?: return@transaction
        graph3.vertices.forEach {
            println(it)
        }
        graph3.edges.forEach {
            println(it)
        }
    }
    transaction {
        val list = repo.loadKeyVerticesResults("pipi")?.forEach {
            println(it)
        }
        repo.loadClusteringResults("pipi")?.forEach {
            println(it)
        }
    }
}