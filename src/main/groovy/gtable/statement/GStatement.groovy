package gtable.statement

import static gtable.util.Util.numeric
import static gtable.util.Util.quote

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
        this
    }

    GStatement oracle() {
        this.processId = includeId
        this.autoIncremental = { sequence ? "${sequence}.nextval," : '' }
        this.selectIdByRowId = { "SELECT * FROM $tableName WHERE rowid=:rowId" }
        this
    }

    String update(Map<String, Object> updating) {
        """UPDATE $tableName SET ${
            updating.collect {
                "${it.key}=${numeric(it.value) ? it.value : quote(it.value)}"
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
        "${autoIncremental()}${values.collect { numeric(it) ? it : quote(it) }.join(COMMA)}"
    }

    private String cols() {
        processId()
        columns?.join(COMMA)
    }
}
