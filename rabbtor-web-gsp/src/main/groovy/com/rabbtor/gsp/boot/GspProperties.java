
package com.rabbtor.gsp.boot;

import com.rabbtor.gsp.config.annotation.GspSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class GspProperties extends GspSettings
{
    @Value("${spring.gsp.tldScanPaths:#{null}}")
    String[] tldScanPaths;

}
