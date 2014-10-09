package gtable.statement

/**
 * Created by Jakim Li on 14-10-9.
 */
class GStatement {
    private static final String COMMA = ','
    String tableName
    List<String> columns
    List<Object> values

    String insert() {
        assert tableName, 'table name cannot be empty'
        assert columns, 'columns cannot be empty'
        insertInto tableName, cols(), vals()
    }

    private String cols() {
        columns.join(COMMA)
    }

    private String vals() {
        values.collect { numeric(it) ? it : quote(it) }.join(COMMA)
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
}
