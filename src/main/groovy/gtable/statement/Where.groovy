package gtable.statement

import static gtable.util.Util.isDate
import static gtable.util.Util.numeric
import static gtable.util.Util.quote

/**
 * Created by Jakim Li on 14-10-13.
 */
class Where {
    static final String TO_DATE_TEMPLATE = '%toDate%'
    String statement

    static eq(Object value) {
        "=${wrapToSql(value)}"
    }

    static Where where(String column, Object value) {
        new Where(statement: "where $column$value")
    }

    @Override
    String toString() {
        statement
    }

    Where and(String column, Object value) {
        statement = "$statement and $column$value"
        this
    }

    def or(String column, Object value) {
        statement = "$statement or $column$value"
        this
    }

    private static wrapToSql(def val) {
        if (numeric(val)) {
            val
        } else if (isDate(val)) {
            "$TO_DATE_TEMPLATE('${val.date}','${val.format}')"
        } else {
            quote(val)
        }
    }

}
