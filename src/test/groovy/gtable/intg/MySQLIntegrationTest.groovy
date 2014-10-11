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
    GTable gTable

    @Before
    void setUp() {
        mysql = new ConfigSlurper().parse(new File('database.groovy').toURI().toURL()).envs.dev.mysql
        sql = Sql.newInstance(mysql.url, mysql.user, mysql.password, mysql.driver)
        gTable = new GTable(sql)
        createDB()
    }

    @Test
    void 'should get auto generated id'() {
        def actualId = gTable.table('animals').save([name: 'dog', age: 13])

        def row = sql.firstRow('select * from animals')
        assert actualId == row.id
        assert 'dog' == row.name
        assert 13 == row.age
    }

    @Test
    void 'should insert into table when there is no auto generated column'() {
        gTable.table('persons').save([name: 'jakim', age: 24])

        def row = sql.firstRow('select * from persons')
        assert 'jakim' == row.name
        assert 24 == row.age
    }

    @After
    void tearDown() {
        destroyDB()
    }

    void destroyDB() {
        sql.execute 'drop table persons;'
        sql.execute 'drop table animals;'
    }

    void createDB() {
        sql.execute '''
            create table persons(
                name varchar(20) not null,
                age int not null
            );
        '''

        sql.execute '''
            CREATE TABLE animals (
                id MEDIUMINT NOT NULL AUTO_INCREMENT,
                name CHAR(30) NOT NULL,
                age int not null,
                PRIMARY KEY (id)
            );
        '''
    }
}
