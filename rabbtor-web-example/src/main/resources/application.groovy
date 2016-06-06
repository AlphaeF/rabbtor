/*
 * /*************************************************************************
 *  *
 *  * RABBYTES LICENSE
 *  * __________________
 *  *
 *  *  [${year]] - ${name}
 *  *  All Rights Reserved.
 *  *
 *  * NOTICE:  All information contained herein is, and remains
 *  * the property of Adobe Systems Incorporated and its suppliers,
 *  * if any.  The intellectual and technical concepts contained
 *  * herein are proprietary to Rabbytes Incorporated
 *  * and its suppliers and may be covered by U.S. and Foreign Patents,
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * from Adobe Systems Incorporated.
 *  */
 */
spring.profiles.active='dev'

test='default'

server {
    port= 8889
}

spring {
    thymeleaf {
        cache= false
        prefix= '/WEB-INF/thymeleaf/'
        enabled= false
        suffix= '.html'
    }
    freemarker {
        enabled = false
        cache = false
        prefix= ''
        suffix = '.ftl'
        templateLoaderPath = '/WEB-INF/ftl/'
        settings {
            api_builtin_enabled = true
        }
    }
    gsp {
        enabled=true
        templateRoots = ['/WEB-INF/gsp/']
    }
    mvc {
        view {
            prefix= '/WEB-INF/jsp/'
            suffix= '.jsp'
        }
    }
}

grails {
    views {
        gsp {
            sitemesh {
                preprocess = false
            }
        }
    }
}

environments {

    prod {
        spring {
            gsp {
                reloadingEnabled = false
                layout {
                    caching = false
                }
            }
        }
    }

    dev {
        spring {
            gsp {
                reloadingEnabled = true
                layout {
                    caching = false
                }
            }
        }
        test = 'dev'
    }


}
