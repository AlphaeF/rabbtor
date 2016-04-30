package com.rabbtor.web.config

import groovy.transform.CompileStatic

@CompileStatic
class RabbtorWebMvcConfigurerComposite implements RabbtorWebMvcConfigurer
{
    private final List<RabbtorWebMvcConfigurer> delegates = new ArrayList<RabbtorWebMvcConfigurer>();

    public void addWebMvcConfigurers(List<RabbtorWebMvcConfigurer> configurers) {
        if (configurers != null) {
            this.delegates.addAll(configurers);
        }
    }
}
