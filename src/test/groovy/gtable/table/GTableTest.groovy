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
        def generatedId = gTable.save([name: 'Jakim', age: 24, birth: '1990-05-01'])

        then:
        1 * sql.executeInsert(_) >> [[1]]
        generatedId == 1
    }

    def "using table to set table name and insert values"() {
        when:
        gTable.table('PERSONS').save([name: 'Jakim', age: 24, birth: '1990-05-01'])

        then:
        1 * sql.executeInsert { it.contains('PERSONS') } >> [[0]]
    }

    def "can override the column name in map"() {
        when:
        gTable.table('PERSONS')
                .columns([name: 'PERSON_NAME'])
                .save([name: 'Jakim', age: 24, birth: '1990-05-01'])

        then:
        1 * sql.executeInsert { it.contains('PERSON_NAME') } >> [[0]]
    }

    def "can select all from table"() {
        when:
        gTable.table('PERSONS').all()

        then:
        1 * sql.eachRow('SELECT * FROM PERSONS', _)
    }
}
