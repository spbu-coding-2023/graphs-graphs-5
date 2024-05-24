package io.sqlite

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GraphEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GraphEntity>(Graphs)
    var name by Graphs.name
    var metadata by Graphs.metadata
}