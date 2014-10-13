package gtable.statement

import spock.lang.Specification

import static gtable.statement.Where.eq
import static gtable.statement.Where.where

/**
 * Created by Jakim Li on 14-10-13.
 */
class WhereTest extends Specification {

    def "should generate eq statement"() {
        expect:
        eq(value) == statement

        where:
        value || statement
        3     || '=3'
        '3'   || """='3'"""
    }

    def 'where should generate a where statement'() {
        expect:
        where(column, eq(value)).toString() == statement

        where:
        column | value   || statement
        'id'   | 3       || 'where id=3'
        'name' | 'jakim' || """where name='jakim'"""
    }

    def 'where should generate a where statement with and statement'() {
        expect:
        where(column, eq(value)).and(column2, eq(value2)).toString() == statement

        where:
        column | value   | column2 | value2   || statement
        'id'   | 3       | 'name'  | 'linjia' || """where id=3 and name='linjia'"""
        'name' | 'jakim' | 'age'   | 19       || """where name='jakim' and age=19"""
    }

    def 'where should generate a where statement with or statement'() {
        expect:
        where(column, eq(value)).or(column2, eq(value2)).toString() == statement

        where:
        column | value   | column2 | value2   || statement
        'id'   | 3       | 'name'  | 'linjia' || """where id=3 or name='linjia'"""
        'name' | 'jakim' | 'age'   | 19       || """where name='jakim' or age=19"""
    }
}
