package gtable.util

/**
 * Created by Jakim Li on 14-10-13.
 */
class Util {

    static boolean numeric(object) {
        object instanceof Number
    }

    static GString quote(object) {
        """'${object.toString()}'"""
    }
}
