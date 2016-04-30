package com.rabbtor.commons.collections


import spock.lang.Specification


class CaseInsensitiveHashMapSpec extends Specification
{
    def 'Test if all methods support case insensitivity'()
    {
        given:
        def map = new CaseInsensitiveHashMap(new Locale("tr"))
        when:
        map.clear()
        map.put('name', 'foo')
        map.put('lastname', 'bar')
        map.put('IşĞÖÇ', 'turkish')
        map.putIfAbsent('nAmE', 'notToBePut')
        map.putIfAbsent('ıŞğöç', 'notToBePut')
        map.put('replace', 0)
        map.replace('ReplAcE', 1)
        map.put('replace2', 100)
        map.replace('RepLacE2', 100, 200)
        map.put('toBeRemoved', 200)
        map.remove('toberemoved')
        map.put('toBeRemoved2', 1)
        map.remove('toberemoved2', 1)

        then:
        map.containsKey('name')
        map.containsKey('NAME')
        map.get('name') == 'foo'
        map.get('nAmE') == 'foo'
        map.get('ıŞĞöç') == 'turkish'
        map.get('replace') == 1
        map.get('REPLACE2') == 200
        !map.containsKey('toBeRemoved')
        !map.containsKey('toBeRemoved2')
        !map.containsKey('TOBEremoved')
        map.name == map.get('name')
        map.NAME == map.get('name')

        // ensure putIfAbsent does not put keys if they are upsent
        def keys = map.keySet().toList()
        keys.contains('name')
        !keys.contains('nAmE')
        !keys.contains('NAME')
        !keys.contains('ıŞğöç')

        when:
        map.clear()
        then:
        map.get('name') == null

    }

    def 'Test compute methods of JDK 1.8'()
    {
        given:
        def map = new CaseInsensitiveHashMap(new Locale("tr"))
        when:
        map.age = 100
        map.height = 175
        map.compute('AgE', { k, v -> 90 })
        map.compute('Hey', { k, v -> 1 })
        map.computeIfAbsent('test', { k -> 1 })
        map.computeIfAbsent('TEsT', { k -> 2 })
        map.computeIfPresent('HEigHT', { k, v -> 200 })

        then:
        map.age == 90
        map.test == 1
        map.TesT == 1
        map.heiGHT == 200
        map.height == 200
        map.heY == map.Hey

        //ensure computeIfAbsent does not put the replaced key
        def keys = map.keySet().toList()
        !keys.contains('TEsT')
    }

    def 'Test keyset removal'()
    {
        given:
        def map = new CaseInsensitiveHashMap(new Locale("tr"))
        when:
        map.age = 100
        map.keySet().remove('AgE')

        then:
        !map.containsKey('age')

    }
}
