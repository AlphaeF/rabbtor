package com.rabbtor.gsp.compiler

import groovy.transform.CompileStatic

@CompileStatic
class GroovyPageParserConfig
{
    private static Map<?,?> map = new HashMap<>();

    static Map<?, ?> get()
    {
        return map
    }

    static void set(Map<?, ?> map)
    {
        GroovyPageParserConfig.map = map ?: [:]
    }
}
