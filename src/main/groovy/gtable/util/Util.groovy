package gtable.util

import gtable.statement.Date

/**
 * Created by Jakim Li on 14-10-13.
 */
class Util {

    static boolean numeric(object) {
        object instanceof Number
    }

    static boolean isDate(val) {
        Date.isInstance(val)
    }

    static GString quote(object) {
        """'${object.toString()}'"""
    }
}
