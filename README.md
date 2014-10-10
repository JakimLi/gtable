gtable [![Build Status](https://travis-ci.org/JakimLi/gtable.svg?branch=master)](https://travis-ci.org/JakimLi/gtable) [![Coverage Status](https://coveralls.io/repos/JakimLi/gtable/badge.png?branch=master)](https://coveralls.io/r/JakimLi/gtable?branch=master)
======

Target to do the simple CRUD to relational database, do it easy and simple based on Groovy built-in SQL

# Quick Start
======
## Install

## Sample
### save to table, default using mysql dialect

    def gTable = new GTable(sql)

    gTable.table('animals').save([name: 'dog', age: 13])

    def row = sql.firstRow('select * from animals')
    assert row.id > 0
    assert row.name == 'dog'
    assert row.age == 13
