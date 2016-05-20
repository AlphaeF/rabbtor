package com.rabbtor.example.web.tags

import com.rabbtor.taglib.TagLib
import com.rabbtor.web.servlet.gsp.tags.TagLibrary

@TagLib
class TestTagLib implements TagLibrary
{
    static namespace = "t"

    static defaultEncodeAs = [taglib:'html']


    Closure write = { attrs, body ->
        out << "Written this into this"
        out << "Go"
        out << "Test"
        out << "Gop"
        out << "<a href='#'>Link from tag </a>"
        null
    }
}
