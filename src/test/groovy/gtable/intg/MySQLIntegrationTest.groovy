package gtable.intg

import groovy.sql.Sql
import gtable.table.GTable
import org.junit.After
import org.junit.Before
import org.junit.Test

import static gtable.statement.Where.eq
import static gtable.statement.Where.where

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
        assert actualId == row.animal_id
        assert 'dog' == row.name
        assert 13 == row.age
    }

    @Test
    void 'can override column name and insert'() {
        def actualId = gTable.table('animals')
                .columns([na: 'name', ag: 'age'])
                .save([na: 'dog', ag: 13])

        def row = sql.firstRow('select * from animals')
        assert actualId == row.animal_id
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

    @Test
    void 'should select all from table'() {
        gTable.table('persons').save([name: 'jakim', age: 24])
        gTable.table('persons').save([name: 'linjia', age: 19])

        def persons = gTable.all()

        assert persons.size() == 2
        assert persons.contains([name: 'jakim', age: 24])
        assert persons.contains([name: 'linjia', age: 19])
    }

    @Test
    void 'should select all to custom column name'() {
        gTable.table('persons').save([name: 'jakim', age: 24])
        gTable.table('persons').save([name: 'linjia', age: 19])

        def persons = gTable.columns([na: 'name']).all()

        assert persons.size() == 2
        assert persons.contains([na: 'jakim', age: 24])
        assert persons.contains([na: 'linjia', age: 19])
    }

    @Test
    void 'should select all to custom id name'() {
        gTable.table('animals').save([name: 'dog', age: 3])

        def persons = gTable.id('animal_id').columns([na: 'name']).all()

        assert persons.size() == 1
        assert persons.contains([id: 1, na: 'dog', age: 3])
    }

    @Test
    void 'should select all to custom id name with other id name'() {
        gTable.table('animals').save([name: 'dog', age: 3])

        def persons = gTable.id('animalId', 'animal_id').columns([na: 'name']).all()

        assert persons.size() == 1
        assert persons.contains([animalId: 1, na: 'dog', age: 3])
    }

    @Test
    void 'should update all table values'() {
        gTable.table('persons').save([name: 'jakim', age: 24])
        gTable.table('persons').save([name: 'linjia', age: 19])

        def persons = gTable.all()

        assert persons.size() == 2
        assert persons.contains([name: 'jakim', age: 24])
        assert persons.contains([name: 'linjia', age: 19])

        gTable.update([age: 25])

        persons = gTable.all()
        assert persons.size() == 2
        assert persons.contains([name: 'jakim', age: 25])
        assert persons.contains([name: 'linjia', age: 25])

        gTable.columns([na: 'name']).update([na: 'newname'])

        persons = gTable.all()
        assert persons.size() == 2
        assert persons.contains([na: 'newname', age: 25])
        assert persons.contains([na: 'newname', age: 25])
    }

    @Test
    void 'should update table values with condition'() {
        gTable.table('persons').save([name: 'jakim', age: 24])
        gTable.table('persons').save([name: 'linjia', age: 19])

        def persons = gTable.all()

        assert persons.size() == 2
        assert persons.contains([name: 'jakim', age: 24])
        assert persons.contains([name: 'linjia', age: 19])

        gTable.update([age: 25], where('name', eq('jakim')))

        persons = gTable.all()
        assert persons.size() == 2
        assert persons.contains([name: 'jakim', age: 25])
        assert persons.contains([name: 'linjia', age: 19])

        gTable.columns([na: 'name']).update([na: 'newname'], where('na', eq('jakim')))

        persons = gTable.all()
        assert persons.size() == 2
        assert persons.contains([na: 'newname', age: 25])
        assert persons.contains([na: 'newname', age: 25])
    }

    @Test
    void 'should update table values with condition with multiple different name'() {
        gTable.table('animals').save([name: 'dog', age: 4])
        gTable.table('animals').save([name: 'cat', age: 9])

        def animals = gTable.id('animal_id').all()

        assert animals.size() == 2
        assert animals.contains([id: 1, name: 'dog', age: 4])
        assert animals.contains([id: 2, name: 'cat', age: 9])

        gTable.columns([na: 'name', ag: 'age']).update([na: 'newname', ag: 3], where('id', eq(1)))

        animals = gTable.all()
        assert animals.size() == 2
        assert animals.contains([id: 1, na: 'newname', ag: 3])
        assert animals.contains([id: 2, na: 'cat', ag: 9])

        gTable.update([na: 'anotherName'], where('id', eq(1)).and('ag', eq(3)))

        animals = gTable.all()
        assert animals.size() == 2
        assert animals.contains([id: 1, na: 'anotherName', ag: 3])
        assert animals.contains([id: 2, na: 'cat', ag: 9])
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
                animal_id MEDIUMINT NOT NULL AUTO_INCREMENT,
                name CHAR(30) NOT NULL,
                age int not null,
                PRIMARY KEY (animal_id)
            );
        '''
    }
}
