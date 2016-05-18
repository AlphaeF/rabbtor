package org.springframework.util


class AntPathMatcherSpec
{
    def 'Test if case insensitive template variable matched'() {
        given:
            def matcher = new AntPathMatcher()
            matcher.caseSensitive = true
        when:
            def path = '/user/{id}'


    }
}
