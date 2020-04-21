/*
 *
 *  * Copyright (c) 2005, 2019, EVECOM Technology Co.,Ltd. All rights reserved.
 *  * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  *
 *
 */

package com.springboot.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        System.out.println("ApplicationRunner");

        //code goes here
    }
}
