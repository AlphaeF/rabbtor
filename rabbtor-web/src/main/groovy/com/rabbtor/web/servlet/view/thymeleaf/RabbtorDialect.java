/**
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
package com.rabbtor.web.servlet.view.thymeleaf;


import com.rabbtor.web.servlet.view.thymeleaf.processor.SpringIncludeTagProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class RabbtorDialect extends AbstractProcessorDialect
{
    public static final String NAME = "Rabbtor";
    public static final String PREFIX = "rbb";
    public static final int PROCESSOR_PRECEDENCE = 999;


    public RabbtorDialect()
    {
        this(PROCESSOR_PRECEDENCE);
    }

    protected RabbtorDialect(int processorPrecedence)
    {
        super(NAME, PREFIX, processorPrecedence);
    }


    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix)
    {

        return new HashSet(Arrays.asList(
                new IProcessor[] { new SpringIncludeTagProcessor(dialectPrefix)}
        ));
    }
}
