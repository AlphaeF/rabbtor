package com.rabbtor.web.servlet.util;

import org.springframework.web.servlet.support.BindStatus;

/**
 * Created by Cagatay on 29.05.2016.
 */
public class BindStatusUtils
{
    public static String getBeanName(BindStatus bindStatus) {
        String beanName;
        int dotPos = bindStatus.getPath().indexOf('.');
        if (dotPos == -1) {
            // property not set, only the object itself
            beanName = bindStatus.getPath();
        }
        else {
            beanName = bindStatus.getPath().substring(0, dotPos);
        }
        return beanName;
    }
}
