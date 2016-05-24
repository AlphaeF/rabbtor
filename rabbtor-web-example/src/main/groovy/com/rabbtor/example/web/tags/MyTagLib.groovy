package com.rabbtor.example.web.tags

import grails.gsp.TagLib

@TagLib
class MyTagLib
{
    static namespace = 'my'

    def testSet = { attrs ->
        g.set(attrs)
        out << "testSet done"
    }
}
