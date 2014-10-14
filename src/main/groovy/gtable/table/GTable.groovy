package gtable.table

import groovy.sql.Sql
import gtable.statement.GStatement
import gtable.statement.Where

/**
 * Created by Jakim Li on 14-10-10.
 */
class GTable {

    static final String REG_WHERE_COL = /\s(.\w+)=/
    String tableName
    GStatement statement = new GStatement()
    Sql sql
    String dialect = 'mysql'
    Map<String, String> overridingCols = [:]
    String idName

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
        userCols(result)
    }

    def find(Where where) {
        def result = []
        statement.tableName = tableName
        sql.eachRow("""${statement."$dialect"().select()} ${overrideWhereCols(where)}""" as String) {
            result << it.toRowResult()
        }
        userCols(result)
    }

    def table(String tableName) {
        this.tableName = tableName
        this
    }

    def id(String idName) {
        this.idName = idName
        this.overridingCols << [id: idName]
        this
    }

    def id(String idKey, String idName) {
        this.idName = idName
        this.overridingCols << [(idKey): idName]
        this
    }

    def update(Map<String, Object> updating) {
        statement.with {
            tableName = this.tableName
        }
        sql.executeUpdate(statement.update(overrideUpdateCols(updating)) as String)
    }

    def update(Map<String, Object> updating, Where where) {
        statement.with {
            tableName = this.tableName
        }
        sql.executeUpdate("${statement.update(overrideUpdateCols(updating))} ${overrideWhereCols(where)}" as String)
    }

    def clear() {
        statement.with {
            tableName = this.tableName
        }
        sql.execute(statement.delete() as String)
    }

    def delete(Where where) {
        statement.with {
            tableName = this.tableName
        }
        sql.execute("${statement.delete()} ${overrideWhereCols(where)}" as String)
    }

    def columns(Map<String, String> cols) {
        overridingCols << cols
        this
    }

    private String overrideWhereCols(Where where) {
        def statement = where.toString()
        (statement =~ (REG_WHERE_COL)).each {
            statement = statement.replaceAll("${it[0]}", " ${(overridingCols."${it[1]}" ?: it[1])}=")
        }
        statement
    }

    private Map overrideUpdateCols(Map<String, Object> updating) {
        updating.inject([:]) { map, it ->
            map << [(overridingCols."${it.key}" ?: it.key): it.value]
        }
    }

    List<String> cols(keys) {
        keys.collect { overridingCols?."$it" ?: it }
    }

    def doInsert = {
        sql.executeInsert(statement."$dialect"().insert() as String).find { true }.find { true }
    }

    @SuppressWarnings('UnnecessaryCollectCall')
    List userCols(List result) {
        result.collect {
            it.inject([:]) { map, that ->
                map << [((overridingCols.find { the -> the.value == that.key } ?: that).key): that.value]
            }
        }
    }
}
