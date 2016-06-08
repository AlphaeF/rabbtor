package com.rabbtor.example.web;

import com.rabbtor.gsp.config.annotation.EnableWebGsp;
import com.rabbtor.gsp.config.annotation.GspSettings;
import com.rabbtor.gsp.config.annotation.WebGspConfigurerAdapter;
import com.rabbtor.gsp.taglib.config.annotation.GspTagLibScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebGsp
@GspTagLibScan
public class GspConfig extends WebGspConfigurerAdapter
{
    @Override
    public void configureGsp(GspSettings config)
    {
        config.setGspReloadingEnabled(true);
    }
}
