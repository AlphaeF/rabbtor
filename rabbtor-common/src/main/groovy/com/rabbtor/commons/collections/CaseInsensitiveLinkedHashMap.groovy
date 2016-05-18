package com.rabbtor.commons.collections

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class CaseInsensitiveLinkedHashMap<T> extends CaseInsensitiveHashMap<T>
{
    @Override
    protected Map createMap()
    {
        return new LinkedHashMap()
    }

    @Override
    protected Map createMap(int initialCapacity)
    {
        return new LinkedHashMap(initialCapacity)
    }
}
