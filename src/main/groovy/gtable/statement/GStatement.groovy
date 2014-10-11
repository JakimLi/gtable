package gtable.statement

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
        "SELECT ${selectCols() ?: '*'} FROM $tableName"
    }

    def selectCols() {
        columns = columns ?: []
        id && columns.add(0, id)
        columns?.join(COMMA)
    }

    private String cols() {
        processId()
        columns?.join(COMMA)
    }

    private String vals() {
        "${autoIncremental()}${values.collect { numeric(it) ? it : quote(it) }.join(COMMA)}"
    }

    private boolean numeric(object) {
        object instanceof Number
    }

    private GString quote(object) {
        """'${object.toString()}'"""
    }

    private String insertInto(def table, def cols, def vals) {
        "INSERT INTO $table($cols) VALUES($vals)"
    }

    def mysql() {
        this
    }

    def oracle() {
        this.processId = {
            columns = columns ?: []
            id && columns?.add(0, id)
        }
        this.autoIncremental = { sequence ? "${sequence}.nextval," : '' }
        this
    }
}
