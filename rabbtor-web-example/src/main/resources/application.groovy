spring.profiles.active='dev'

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

environments {

    prod {
        reload = false
        cache = true
    }

    dev {
        spring {
            gsp {
                reload = true
                devmode = true
                cache = false
            }
        }
    }


}
