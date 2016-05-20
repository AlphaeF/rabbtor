spring.profiles.active='dev'

server {
    port= 8889
}

spring {
    thymeleaf {
        cache= false
        prefix= '/WEB-INF/thymeleaf/'
        enabled= true
        suffix= '.html'
    }
    freemarker {
        enabled = true
        cache = false
        prefix= ''
        suffix = '.ftl'
        templateLoaderPath = '/WEB-INF/ftl/'
        settings {
            api_builtin_enabled = true
        }
    }
    gsp {
        templateRoots = ['/']
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
