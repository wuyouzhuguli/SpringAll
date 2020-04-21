/*
 *
 *  * Copyright (c) 2005, 2019, EVECOM Technology Co.,Ltd. All rights reserved.
 *  * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  *
 *
 */

package com.springboot.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        System.out.println("ApplicationReadyEvent");

        return;
    }
}
