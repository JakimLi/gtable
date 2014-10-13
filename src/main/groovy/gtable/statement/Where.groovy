package gtable.statement

import static gtable.util.Util.numeric
import static gtable.util.Util.quote

/**
 * Created by Jakim Li on 14-10-13.
 */
class Where {
    String statement

    static eq(Object value) {
        "=${numeric(value) ? value : quote(value)}"
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
}
