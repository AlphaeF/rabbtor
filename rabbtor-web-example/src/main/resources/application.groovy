server {
    port= 8888
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
        reloadingEnabled = true
        templateRoots = '/WEB-INF/gsp'
        layout {
            caching = false
        }
        removeDefaultViewResolver = false
        replaceViewResolverBean = false
    }
    mvc {
        view {
            prefix= '/WEB-INF/jsp/'
            suffix= '.jsp'
        }
    }
}

environments {
    'default' {
        server.port = 8889
    }
}