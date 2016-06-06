
package com.rabbtor.gsp.config.annotation;


import com.rabbtor.gsp.jsp.TagLibraryResolverImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public abstract class GspJspConfigurationSupport
{
    @Bean(autowire = Autowire.BY_NAME)
    public TagLibraryResolverImpl jspTagLibraryResolver()
    {
        TagLibraryResolverImpl resolver = new TagLibraryResolverImpl();
        resolver.setTldScanPatterns(getTldScanPaths());
        return resolver;
    }


    protected void registerTldScanPaths(List<String> paths) {

    }

    public String[] getTldScanPaths()
    {
        List<String> tldScanPaths = new ArrayList<>();

        Collection<String> defaultPaths = Arrays.asList("classpath*:/META-INF/spring*.tld,classpath*:/META-INF/fmt.tld,classpath*:/META-INF/c.tld,classpath*:/META-INF/rabbtor*.tld,classpath*:/META-INF/c-1_0-rt.tld"
                .split(","));

        for(String defaultPath : defaultPaths ) {
            tldScanPaths.add(defaultPath.trim());
        }

        registerTldScanPaths(tldScanPaths);
        return tldScanPaths.toArray(new String[tldScanPaths.size()]);
    }
}
