/*
 * Copyright 2004-2005 Graeme Rocher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabbtor.gsp;

import com.rabbtor.taglib.encoder.OutputContextLookup;
import com.rabbtor.taglib.encoder.OutputContextLookupHelper;
import groovy.lang.Writable;
import groovy.text.Template;


import java.util.Map;

/**
 * Knows how to make in instance of GroovyPageWritable.
 *
 * @author Graeme Rocher
 * @since 0.5
 */
public class GroovyPageTemplate implements Template, Cloneable
{

    private GroovyPageMetaInfo metaInfo;
    private boolean allowSettingContentType = false;

    public GroovyPageTemplate(GroovyPageMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public Writable make() {
        return make(null,null);
    }

    public Writable make(OutputContextLookup outputContextLookup) {
        return make(null,outputContextLookup);
    }

    public Writable make(Map binding, OutputContextLookup outputContextLookup) {
        if (outputContextLookup == null)
            outputContextLookup = OutputContextLookupHelper.getOutputContextLookup();

        GroovyPageWritable gptw = new GroovyPageWritable(metaInfo, outputContextLookup, allowSettingContentType);
        if (binding != null)
            gptw.setBinding(binding);
        return gptw;
    }


    @SuppressWarnings("rawtypes")
    public Writable make(Map binding) {
        return make(binding,null);
    }

    public GroovyPageMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public boolean isAllowSettingContentType() {
        return allowSettingContentType;
    }

    public void setAllowSettingContentType(boolean allowSettingContentType) {
        this.allowSettingContentType = allowSettingContentType;
    }
    
    @Override
    public Object clone() {
        GroovyPageTemplate cloned = new GroovyPageTemplate(metaInfo);
        cloned.setAllowSettingContentType(allowSettingContentType);
        return cloned;
    }
}
