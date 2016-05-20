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


import com.rabbtor.web.servlet.mvc.RabbtorWebRequest;


public class WebRequestOutputContext extends WebOutputContextLookup.WebOutputContext {
    private final RabbtorWebRequest webRequest;

    public static final String PAGE_SCOPE="com.rabbtor.PAGE_SCOPE";

    public WebRequestOutputContext(RabbtorWebRequest webRequest) {
        this.webRequest = webRequest;
    }

    protected RabbtorWebRequest lookupWebRequest() {
        return webRequest;
    }
}
