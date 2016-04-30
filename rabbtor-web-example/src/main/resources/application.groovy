server {
    port= 8888
}

spring {
    thymeleaf {
        cache= false
        prefix= '/WEB-INF/templates'
        enabled= true
        suffix= '.html'
    }
}

environments {
    'default' {
        server.port = 8889
    }
}