# gtable [![Build Status](https://travis-ci.org/JakimLi/gtable.svg?branch=master)](https://travis-ci.org/JakimLi/gtable) [![Coverage Status](https://coveralls.io/repos/JakimLi/gtable/badge.png?branch=master)](https://coveralls.io/r/JakimLi/gtable?branch=master)
======

Target to do the simple CRUD to relational database, do it easy and simple based on Groovy built-in SQL

## Quick Start
======
### Install

======
### Sample
#### Save or Insert
you can save a map to a table, default using mysql dialect, will use the key as the column name

    def gTable = new GTable(sql)

    gTable.table('animals').save([name: 'dog', age: 13])

    def row = sql.firstRow('select * from animals')
    assert row.id > 0
    assert row.name == 'dog'
    assert row.age == 13

#### Auto incremental id
if database generate an id, for example auto incremental internal id, save method will return it

    def generatedId = gTable.table('animals').save([name: 'dog', age: 13])

    assert generatedId > 0

#### Custom column names
you can override the column name use columns method

    gTable.table('animals').columns([name: 'ANIMAL_NAME']).save([name: 'dog', age: 13])


#### read all records form table

    def persons = gTable.table('persons').all()

    assert persons.size() == 2
    assert persons.contains([name: 'jakim', age: 24])
    assert persons.contains([name: 'linjia', age: 19])

default, the returned map will use the column name in database as key, you can override this behavior

    def persons = gTable.columns([na: 'name']).all()

    assert persons.size() == 2
    assert persons.contains([na: 'jakim', age: 24])
    assert persons.contains([na: 'linjia', age: 19])

for the id column, either you can define it just like any other columns, or you can use id to change the id column's key:

    def animals = gTable.id('animal_id').columns([na: 'name']).all()
    assert persons.size() == 1
    assert persons[0] == [id: 1, na: 'dog']
    
    def animals = gTable.id('animalId', 'animal_id').columns([na: 'name']).all()
    assert persons.size() == 1
    assert persons[0] == [animalId: 1, na: 'dog']
    
    
#### update values
You can update all records at one time

    gTable.update([age: 25])
    
    persons = gTable.all()
    assert persons.size() == 2
    assert persons.contains([name: 'jakim', age: 25])
    assert persons.contains([name: 'linjia', age: 25])
    
You can also update records with specific condition using where

    import static gtable.statement.Where.eq
    import static gtable.statement.Where.where

    gTable.update([age: 25], where('name', eq('jakim')))
    
    persons = gTable.all()
    assert persons.size() == 2
    assert persons.contains([name: 'jakim', age: 25])
    assert persons.contains([name: 'linjia', age: 19])

You can use and/or to specify more than one condition

    gTable.update([age: 25], where('name', eq('jakim')).and('id', eq(3)))
    
After you override columns with columns method, you need to use the overriding column name in where any where statement

    gTable.id('animal_id').columns([na: 'name', ag: 'age']).update([na: 'newname', ag: 3], where('id', eq(1)))
    
    gTable.update([na: 'anotherName'], where('id', eq(1)).and('ag', eq(3)))