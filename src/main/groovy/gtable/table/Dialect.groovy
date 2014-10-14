package gtable.table

/**
 * Created by Jakim Li on 14-10-14.
 */
enum Dialect {
    MYSQL, ORACLE

    String toString() {
        name().toLowerCase()
    }
}
