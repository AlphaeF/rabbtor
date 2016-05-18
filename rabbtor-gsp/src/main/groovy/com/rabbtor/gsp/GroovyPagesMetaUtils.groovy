/*
 * Copyright 2011 the original author or authors.
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
package com.rabbtor.gsp

import com.rabbtor.taglib.TagLibraryLookup
import com.rabbtor.taglib.TagLibraryMetaUtils
import com.rabbtor.util.MetaClassUtils
import groovy.transform.CompileStatic


@CompileStatic
class GroovyPagesMetaUtils {

    static void registerMethodMissingForGSP(Class gspClass, TagLibraryLookup gspTagLibraryLookup,boolean developmentMode) {
        registerMethodMissingForGSP(MetaClassUtils.getExpandoMetaClass(gspClass), gspTagLibraryLookup,developmentMode)
    }

    static void registerMethodMissingForGSP(final MetaClass emc, final TagLibraryLookup gspTagLibraryLookup, boolean developmentMode) {
        if(gspTagLibraryLookup==null) return
        final boolean addMethodsToMetaClass = !developmentMode

        GroovyObject mc = (GroovyObject)emc
        synchronized(emc) {
            mc.setProperty("methodMissing", { String name, Object args ->
                TagLibraryMetaUtils.methodMissingForTagLib(emc, emc.getTheClass(), gspTagLibraryLookup, GroovyPage.DEFAULT_NAMESPACE, name, args, addMethodsToMetaClass)
            })
        }
        TagLibraryMetaUtils.registerTagMetaMethods(emc, gspTagLibraryLookup, GroovyPage.DEFAULT_NAMESPACE)
        TagLibraryMetaUtils.registerNamespaceMetaProperties(emc, gspTagLibraryLookup)
    }


}