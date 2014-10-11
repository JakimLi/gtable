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

    List<String> cols(keys) {
        keys.collect { overridingCols?."$it" ?: it }
    }

    def doInsert = {
        sql.executeInsert(statement."$dialect"().insert()).find { true }.find { true }
    }

    def table(String tableName) {
        this.tableName = tableName
        this
    }

    def columns(Map<String, String> cols) {
        overridingCols = cols
        this
    }
}
