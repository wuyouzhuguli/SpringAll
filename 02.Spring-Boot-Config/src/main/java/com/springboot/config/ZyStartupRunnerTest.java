/*
 *
 *  * Copyright (c) 2005, 2019, EVECOM Technology Co.,Ltd. All rights reserved.
 *  * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  *
 *
 */

package com.springboot.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by CavanLiu on 2017/2/28 0028.
 */
@Component
@Order(value = 1)
public class ZyStartupRunnerTest implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunner");
    }
}
