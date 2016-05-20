package com.rabbtor.web.servlet.gsp.tags

import com.rabbtor.taglib.GspTagException
import com.rabbtor.taglib.TagLibraryLookup
import com.rabbtor.taglib.TagLibraryMetaUtils
import com.rabbtor.taglib.TagOutput
import com.rabbtor.taglib.TemplateVariableBinding
import com.rabbtor.taglib.encoder.OutputEncodingStack
import com.rabbtor.taglib.encoder.WithCodecHelper
import com.rabbtor.util.MetaClassUtils
import com.rabbtor.web.servlet.mvc.RabbtorWebRequest
import com.rabbtor.web.taglib.WebRequestTemplateVariableBinding
import com.rabbtor.web.taglib.encoder.WebRequestOutputContext
import groovy.transform.CompileDynamic
import org.codehaus.groovy.runtime.InvokerHelper
import org.grails.buffer.GrailsPrintWriter
import org.grails.encoder.Encoder
import org.springframework.web.context.request.RequestAttributes

import javax.annotation.PostConstruct


trait TagLibrary extends TagLibraryInvoker
{
    private Encoder rawEncoder

    @PostConstruct
    void initializeTagLibrary() {
        if (!isDevelopmentMode())
            TagLibraryMetaUtils.enhanceTagLibMetaClass(MetaClassUtils.getExpandoMetaClass(getClass()), tagLibraryLookup, getTaglibNamespace())
    }

    @CompileDynamic
    def raw(Object value) {
        if (rawEncoder == null) {
            rawEncoder = WithCodecHelper.lookupEncoder(getApplicationContext(), "Raw")
            if(rawEncoder == null)
                return InvokerHelper.invokeMethod(value, "encodeAsRaw", null)
        }
        return rawEncoder.encode(value)
    }

    /**
     * Throws a GrailsTagException
     *
     * @param message The error message
     */
    void throwTagError(String message) {
        throw new GspTagException(message)
    }

    String getTaglibNamespace() {
        if(hasProperty('namespace')) {
            return ((GroovyObject)this).getProperty('namespace')
        }
        return TagOutput.DEFAULT_NAMESPACE
    }

    /**
     * Obtains the page scope instance
     *
     * @return  The page scope instance
     */
    TemplateVariableBinding getPageScope() {
        RabbtorWebRequest webRequest = getWebRequest()
        TemplateVariableBinding binding = (TemplateVariableBinding) webRequest.getAttribute(WebRequestOutputContext.PAGE_SCOPE,
                RequestAttributes.SCOPE_REQUEST)
        if (binding == null) {
            binding = new TemplateVariableBinding(new WebRequestTemplateVariableBinding(webRequest))
            binding.root = true
            webRequest.setAttribute(WebRequestOutputContext.PAGE_SCOPE, binding, RequestAttributes.SCOPE_REQUEST)
        }
        binding
    }

    /**
     * Obtains the currently output writer

     * @return The writer to use
     */
    GrailsPrintWriter getOut() {
        OutputEncodingStack.currentStack().taglibWriter
    }

    /**
     * Sets the current output writer
     * @param newOut The new output writer
     */
    void setOut(Writer newOut) {
        OutputEncodingStack.currentStack().push(newOut,true)
    }


    /**
     * Property missing implementation that looks up tag library namespaces or tags in the default namespace
     *
     * @param name The property name
     * @return A tag namespace or a tag in the default namespace
     *
     * @throws MissingPropertyException When no tag namespace or tag is found
     */
    Object propertyMissing(String name) {
        TagLibraryLookup gspTagLibraryLookup = getTagLibraryLookup();
        if (gspTagLibraryLookup != null) {

            Object result = gspTagLibraryLookup.lookupNamespaceDispatcher(name);
            if (result == null) {
                String namespace = getTaglibNamespace()
                GroovyObject tagLibrary = gspTagLibraryLookup.lookupTagLibrary(namespace, name);
                if (tagLibrary == null) {
                    tagLibrary = gspTagLibraryLookup.lookupTagLibrary(TagOutput.DEFAULT_NAMESPACE, name);
                }

                if (tagLibrary != null) {
                    Object tagProperty = tagLibrary.getProperty(name);
                    if (tagProperty instanceof Closure) {
                        result = ((Closure<?>)tagProperty).clone();
                    }
                }
            }

            if (result != null && developmentMode) {
                MetaClass mc = MetaClassUtils.getExpandoMetaClass(getClass())
                TagLibraryMetaUtils.registerPropertyMissingForTag(mc, name, result);
            }

            if (result != null) {
                return result;
            }
        }

        throw new MissingPropertyException(name, this.getClass());
    }

}
