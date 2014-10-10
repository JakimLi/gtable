package gtable.intg

import groovy.sql.Sql
import gtable.table.GTable
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Jakim Li on 14-10-10.
 */
class MySQLIntegrationTest {
    ConfigObject mysql
    Sql sql

    @Before
    void setUp() {
        mysql = new ConfigSlurper().parse(new File('database.groovy').toURI().toURL()).envs.dev.mysql
        sql = Sql.newInstance(mysql.url, mysql.user, mysql.password, mysql.driver)
        cleanDB()
    }

    @Test
    void 'should insert record to database'() {
        def gTable = new GTable(sql)

        gTable.table('animals').save([name: 'dog', age: 13])

        def row = sql.firstRow('select * from animals')
        assert row.id > 0
        assert row.name == 'dog'
        assert row.age == 13
    }

    @After
    void tearDown() {
        cleanDB()
    }

    private boolean cleanDB() {
        sql.execute('delete from animals')
    }

}
