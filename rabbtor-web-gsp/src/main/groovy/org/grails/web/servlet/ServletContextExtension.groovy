
package org.grails.web.servlet

import javax.servlet.ServletContext

/**
 * An extension that adds methods to the {@link ServletContext} interface
 *
 * @author Jeff Brown
 * @since 3.0
 */
class ServletContextExtension {

    static propertyMissing(ServletContext context, String name, value) {
        context.setAttribute name, value
    }
    
    static propertyMissing(ServletContext context, String name) {
        context.getAttribute name
    }
}
