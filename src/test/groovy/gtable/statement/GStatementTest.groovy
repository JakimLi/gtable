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

    def "oracle should generate insert statement and has auto incremental id"() {
        given:
        statement.with {
            tableName = 'PERSONS'
            columns = ['NAME', 'AGE']
            values = ['Jakim', 24]
            id = 'PERSON_ID'
            sequence = 'SEQ_PERSONS'
        }

        when:
        def insertStatement = statement.oracle().insert()

        then:
        insertStatement == '''INSERT INTO PERSONS(PERSON_ID,NAME,AGE) VALUES(SEQ_PERSONS.nextval,'Jakim',24)'''
    }

    def "mysql has id should generated correct insert sql even if id presented"() {
        given:
        statement.with {
            tableName = 'PERSONS'
            columns = ['NAME', 'AGE']
            values = ['Jakim', 24]
            id = 'PERSON_ID'
        }

        when:
        def insertStatement = statement.mysql().insert()

        then:
        insertStatement == '''INSERT INTO PERSONS(NAME,AGE) VALUES('Jakim',24)'''
    }

    def "can generate select sql statement"() {
        given:
        statement.with {
            tableName = 'PERSONS'
            columns = cols
        }

        expect:
        statement."$dialect"().select() == state

        where:

        cols            | dialect  || state
        ['name', 'age'] | 'mysql'  || 'SELECT * FROM PERSONS'
        ['name', 'age'] | 'oracle' || 'SELECT * FROM PERSONS'
        []              | 'oracle' || 'SELECT * FROM PERSONS'
        null            | 'oracle' || 'SELECT * FROM PERSONS'
    }

    def "can generate update sql statment"() {
        statement.with {
            tableName = 'PERSONS'
        }

        expect:
        statement.update(updating) == state

        where:
        updating || state
        [name: 'jakim'] || """UPDATE PERSONS SET name='jakim'"""
        [age: 3] || 'UPDATE PERSONS SET age=3'
        [name: 'jakim', age: 3] || """UPDATE PERSONS SET name='jakim',age=3"""
    }
}
