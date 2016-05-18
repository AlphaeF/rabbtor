package com.rabbtor.gsp.compiler;


import com.rabbtor.RabbtorEnvironment;
import com.rabbtor.taglib.encoder.DefaultOutputEncodingSettings;
import com.rabbtor.taglib.encoder.OutputEncodingSettings;
import org.springframework.core.io.Resource;

import java.io.File;

public interface GspConfiguration
{
    boolean isSitemeshPreprocessingEnabled();
    RabbtorEnvironment getEnvironment();
    OutputEncodingSettings getOutputEncodingSettings();
    String getDefaultEncoding();
    String getGeneratedDirectoryLocation();
    boolean isReloadEnabled();
}
