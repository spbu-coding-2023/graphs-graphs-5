package view

data class DBInput(
    var dBType: String = "",
    var isUpdatedSql: Boolean = false,
    var pathToDb: String = "",
    var name: String = "",
    var isUpdatedNeo4j: Boolean = false,
    var uri: String = "",
    var login: String = "",
    var password: String = "",
    var isUndirected: Boolean = false
)