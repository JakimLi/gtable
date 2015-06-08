package gtable.statement

import gtable.util.Util

import static gtable.table.Dialect.MYSQL
import static gtable.table.Dialect.ORACLE

/**
 * Created by Jakim Li on 14-10-9.
 */
class GStatement {
    private static final String COMMA = ','
    String tableName
    List<String> columns
    List<Object> values
    String id
    String sequence
    private Closure processId = { '' }
    private Closure autoIncremental = { '' }
    private Closure selectIdByRowId = { }
    private Closure stringToDate = { }
    private Closure includeId = {
        columns = columns ?: []
        id && columns.add(0, id)
    }

    String insert() {
        assert tableName, 'table name cannot be empty'
        assert columns, 'columns cannot be empty'
        insertInto tableName, cols(), vals()
    }

    String select() {
        "SELECT * FROM $tableName"
    }

    GStatement mysql() {
        this.stringToDate = { val -> "${MYSQL.toDate}('${val.date}','${val.format}')" }
        this
    }

    GStatement oracle() {
        this.processId = includeId
        this.autoIncremental = { sequence ? "${sequence}.nextval," : '' }
        this.selectIdByRowId = { "SELECT * FROM $tableName WHERE rowid=:rowId" }
        this.stringToDate = { val -> "${ORACLE.toDate}('${val.date}','${val.fomat}')" }
        this
    }

    String update(Map<String, Object> updating) {
        """UPDATE $tableName SET ${
            updating.collect {
                "${it.key}=${wrapToSql(it.value)}"
            }.join(COMMA)
        }"""
    }

    String delete() {
        "DELETE FROM $tableName"
    }

    private String insertInto(def table, def cols, def vals) {
        "INSERT INTO $table($cols) VALUES($vals)"
    }

    private String vals() {
        "${autoIncremental()}${values.collect { wrapToSql(it) }.join(COMMA)}"
    }

    private String cols() {
        processId()
        columns?.join(COMMA)
    }

    private wrapToSql(def val) {
        if (Util.numeric(val)) {
            val
        } else if (Util.isDate(val)) {
            stringToDate(val)
        } else {
            Util.quote(val)
        }
    }
}
