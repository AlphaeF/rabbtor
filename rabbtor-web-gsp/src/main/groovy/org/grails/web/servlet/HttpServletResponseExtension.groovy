
package org.grails.web.servlet

import javax.servlet.http.HttpServletResponse

/**
 * 
 * @author Jeff Brown
 * @since 3.0
 * 
 */
class HttpServletResponseExtension {
    static leftShift(HttpServletResponse response, arg) {
        response.writer << arg
    }
}
