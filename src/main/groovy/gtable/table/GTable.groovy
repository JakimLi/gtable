package gtable.table

import groovy.sql.Sql
import gtable.statement.GStatement

/**
 * Created by Jakim Li on 14-10-10.
 */
class GTable {

    String tableName
    GStatement statement = new GStatement()
    Sql sql
    String dialect = 'mysql'
    Map<String, String> overridingCols

    GTable(Sql sql) {
        this.sql = sql
    }

    def save(Map<String, Object> vals) {
        statement.with {
            tableName = this.tableName
            columns = cols(vals.keySet())
            values = vals*.value
        }
        doInsert()
    }

    def all() {
        def result = []
        statement.tableName = tableName
        sql.eachRow(statement."$dialect"().select() as String) {
            result << it.toRowResult()
        }
        userDefinedCols(result)
    }

    List<String> cols(keys) {
        keys.collect { overridingCols?."$it" ?: it }
    }

    def doInsert = {
        sql.executeInsert(statement."$dialect"().insert() as String).find { true }.find { true }
    }

    def table(String tableName) {
        this.tableName = tableName
        this
    }

    def columns(Map<String, String> cols) {
        overridingCols = cols
        this
    }

    List userDefinedCols(List result) {
        def cols = []
        result.each {
            cols << it.inject([:]) { map, that ->
                def col = overridingCols.find { the -> the.value == that.key }
                map << [((col ?: that).key): that.value]
            }
        }
        cols
    }
}
