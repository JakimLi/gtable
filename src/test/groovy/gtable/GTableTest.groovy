package gtable

import org.junit.Test


class GTableTest {
    GTable gTable = new GTable()

    @Test
    def void 'specify table name in string'() {
        when:
        GTable sameTable = gTable.table('TABLE_NAME')

        then:
        gTable.tableName == 'TABLE_NAME'
        sameTable instanceof GTable
        sameTable.tableName == 'TABLE_NAME'
    }
}
