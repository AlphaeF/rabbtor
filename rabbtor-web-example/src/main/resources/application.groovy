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
