package gtable.table

import groovy.sql.Sql
import spock.lang.Specification

/**
 * Created by Jakim Li on 14-10-10.
 */
class GTableTest extends Specification {

    Sql sql = Mock()
    GTable gTable = new GTable(sql)

    def "using default columns names to insert values"() {
        given:
        gTable.with {
            tableName = 'PERSONS'
        }

        when:
        gTable.save([name: 'Jakim', age: 24, birth: '1990-05-01'])

        then:
        1 * sql.executeInsert(_)
    }
}
