package com.rabbtor.web.servlet.view.thymeleaf;


import com.rabbtor.web.servlet.view.thymeleaf.processor.SpringIncludeTagProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


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
        return Arrays.asList(

                new IProcessor[] { new SpringIncludeTagProcessor(dialectPrefix)}

        ).stream().collect(Collectors.toSet());
    }
}
