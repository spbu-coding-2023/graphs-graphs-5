package io.sqlite

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VertexEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VertexEntity>(Vertices)
    var data by Vertices.data
    var graph by GraphEntity referencedOn Vertices.graph
}