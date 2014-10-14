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
    def processId = { '' }
    def autoIncremental = { '' }

    String insert() {
        assert tableName, 'table name cannot be empty'
        assert columns, 'columns cannot be empty'
        insertInto tableName, cols(), vals()
    }

    def select() {
        "SELECT * FROM $tableName"
    }

    def includeId = {
        columns = columns ?: []
        id && columns.add(0, id)
    }

    private String cols() {
        processId()
        columns?.join(COMMA)
    }

    private String vals() {
        "${autoIncremental()}${values.collect { numeric(it) ? it : quote(it) }.join(COMMA)}"
    }

    private String insertInto(def table, def cols, def vals) {
        "INSERT INTO $table($cols) VALUES($vals)"
    }

    def mysql() {
        this
    }

    def oracle() {
        this.processId = includeId
        this.autoIncremental = { sequence ? "${sequence}.nextval," : '' }
        GStatement.metaClass.selectIdByRowId { "SELECT * FROM $tableName WHERE rowid=:rowId" }
        this
    }

    def update(Map<String, Object> updating) {
        """UPDATE $tableName SET ${
            updating.collect {
                "${it.key}=${numeric(it.value) ? it.value : quote(it.value)}"
            }.join(COMMA)
        }"""
    }

    def delete() {
        "DELETE FROM $tableName"
    }
}
