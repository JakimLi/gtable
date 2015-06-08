package gtable.table

/**
 * Created by Jakim Li on 14-10-14.
 */
enum Dialect {
    MYSQL {
        String toDate = 'STR_TO_DATE'
    } , ORACLE {
        String toDate = 'TO_DATE'
    }
    String toString() {
        name().toLowerCase()
    }
}

