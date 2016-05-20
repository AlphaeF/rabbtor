/*
 * Copyright 2015 the original author or authors.
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

package com.rabbtor.web.taglib.encoder;

import com.rabbtor.taglib.AbstractTemplateVariableBinding;
import com.rabbtor.taglib.encoder.DefaultOutputContextLookup;
import com.rabbtor.taglib.encoder.OutputContext;
import com.rabbtor.taglib.encoder.OutputContextLookup;
import com.rabbtor.taglib.encoder.OutputEncodingStack;
import com.rabbtor.web.servlet.mvc.RabbtorWebRequest;
import com.rabbtor.web.taglib.WebRequestTemplateVariableBinding;
import com.rabbtor.web.util.WebUtils;
import org.grails.encoder.DefaultEncodingStateRegistry;
import org.grails.encoder.EncodingStateRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class WebOutputContextLookup implements OutputContextLookup, Ordered
{
    private static final WebOutputContext webOutputContext = new WebOutputContext();
    static final String ATTRIBUTE_NAME_OUTPUT_STACK="org.grails.web.encoder.OUTPUT_ENCODING_STACK";

    @Override
    public OutputContext lookupOutputContext() {

        if (RequestContextHolder.currentRequestAttributes() != null)
            return webOutputContext;
        return new DefaultOutputContextLookup.DefaultOutputContext();

    }

    @Override
    public int getOrder() {
        return 0;
    }

    static class WebOutputContext implements OutputContext {
        WebOutputContext() {

        }

        @Override
        public EncodingStateRegistry getEncodingStateRegistry() {
            WebRequest request = lookupWebRequest();
            EncodingStateRegistry registry = (EncodingStateRegistry) request.getAttribute(EncodingStateRegistry.class.getName(),RequestAttributes.SCOPE_REQUEST);
            if (registry == null) {
                registry = new DefaultEncodingStateRegistry();
                request.setAttribute(EncodingStateRegistry.class.getName(),registry,RequestAttributes.SCOPE_REQUEST);
            }
            return registry;
        }

        @Override
        public void setCurrentOutputEncodingStack(OutputEncodingStack outputEncodingStack) {
            lookupWebRequest().setAttribute(ATTRIBUTE_NAME_OUTPUT_STACK, outputEncodingStack, RequestAttributes.SCOPE_REQUEST);
        }

        @Override
        public OutputEncodingStack getCurrentOutputEncodingStack() {
            return (OutputEncodingStack) lookupWebRequest().getAttribute(ATTRIBUTE_NAME_OUTPUT_STACK, RequestAttributes.SCOPE_REQUEST);
        }

        @Override
        public Writer getCurrentWriter() {
            return lookupWebRequest().getOut();
        }

        @Override
        public void setCurrentWriter(Writer currentWriter) {
            lookupWebRequest().setOut(currentWriter);
        }

        @Override
        public AbstractTemplateVariableBinding createAndRegisterRootBinding() {
            AbstractTemplateVariableBinding binding = new WebRequestTemplateVariableBinding(lookupWebRequest());
            setBinding(binding);
            return binding;
        }

        @Override
        public AbstractTemplateVariableBinding getBinding() {
            return (AbstractTemplateVariableBinding)lookupWebRequest().getAttribute(WebRequestOutputContext.PAGE_SCOPE, RequestAttributes.SCOPE_REQUEST);
        }

        @Override
        public void setBinding(AbstractTemplateVariableBinding binding) {
            lookupWebRequest().setAttribute(WebRequestOutputContext.PAGE_SCOPE, binding, RequestAttributes.SCOPE_REQUEST);
        }

        @Override
        public void setContentType(String contentType) {
            lookupResponse().setContentType(contentType);
        }

        @Override
        public boolean isContentTypeAlreadySet() {
            HttpServletResponse response = lookupResponse();
            return response.isCommitted() || response.getContentType() != null;
        }

        @Override
        public ApplicationContext getApplicationContext()
        {
            return lookupWebRequest().getApplicationContext();
        }

        protected RabbtorWebRequest lookupWebRequest() {
            return RabbtorWebRequest.lookup();
        }

        protected HttpServletResponse lookupResponse() {
            return lookupWebRequest().getResponse();
        }
    }
}
