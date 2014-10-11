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