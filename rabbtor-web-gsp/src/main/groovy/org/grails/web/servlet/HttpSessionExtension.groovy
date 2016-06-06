
package org.grails.web.servlet

import groovy.transform.CompileStatic

import javax.servlet.http.HttpSession


/**
 *
 * Methods added to the {@link HttpSession} interface
 *
 * @author Jeff Brown
 * @author Graeme Rocher
 *
 * @since 3.0
 * 
 */
@CompileStatic
class HttpSessionExtension {
    
    static getProperty(HttpSession session, String name) {
        def mp = session.class.metaClass.getMetaProperty(name)
        return mp ? mp.getProperty(session) : session.getAttribute(name)
    }
    
    static propertyMissing(HttpSession session, String name, value) {
        session.setAttribute name, value    
    }
        
    static getAt(HttpSession session, String name) {
        getProperty session, name
    }
    
    static propertyMissing(HttpSession session, String name) {
        getProperty session, name
    }
}
