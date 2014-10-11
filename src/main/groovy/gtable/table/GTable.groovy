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

    GTable(Sql sql) {
        this.sql = sql
    }

    def save(Map<String, Object> vals) {
        statement.with {
            tableName = this.tableName
            columns = vals*.key
            values = vals*.value
        }
        doInsert()
    }

    def doInsert = {
        sql.executeInsert(statement."$dialect"().insert()).find { true }.find { true }
    }

    def table(String tableName) {
        this.tableName = tableName
        this
    }
}
