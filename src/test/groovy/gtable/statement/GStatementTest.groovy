package gtable.statement

import spock.lang.Specification

/**
 * Created by Jakim Li on 14-10-9.
 */
class GStatementTest extends Specification {

    GStatement statement = new GStatement()

    def "should throw exception when table name is not specified"() {
        when:
        statement.insert()

        then:
        def error = thrown(Error)
        error.message.contains 'table name cannot be empty'
    }

    def "should throw exception when table name is set to empty string"() {
        given:
        statement.with {
            tableName = ''
        }

        when:
        statement.insert()

        then:
        def error = thrown(Error)
        error.message.contains 'table name cannot be empty'
    }

    def "should throw exception when no columns given"() {
        given:
        statement.with {
            tableName = 'TABLE'
        }

        when:
        statement.insert()

        then:
        def error = thrown(Error)
        error.message.contains 'columns cannot be empty'
    }

    def "should throw exception when zero columns given"() {
        given:
        statement.with {
            tableName = 'TABLE'
            columns = []
        }

        when:
        statement.insert()

        then:
        def error = thrown(Error)
        error.message.contains 'columns cannot be empty'
    }

    def "should generate insert statement default as oracle dialect"() {
        given:
        statement.with {
            tableName = 'PERSONS'
            columns = cols
            values = vals
        }

        expect:
        statement.insert() == states

        where:
        cols             | vals         || states
        ['COL']          | [3]          || '''INSERT INTO PERSONS(COL) VALUES(3)'''
        ['COL']          | ['VAL']      || '''INSERT INTO PERSONS(COL) VALUES('VAL')'''
        ['COL1', 'COL2'] | [1, 2]       || '''INSERT INTO PERSONS(COL1,COL2) VALUES(1,2)'''
        ['COL1', 'COL2'] | [1, '2']     || '''INSERT INTO PERSONS(COL1,COL2) VALUES(1,'2')'''
        ['COL1', 'COL2'] | ['V1', 'V2'] || '''INSERT INTO PERSONS(COL1,COL2) VALUES('V1','V2')'''
    }
}
