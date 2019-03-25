/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubboadmin.web.mvc.governance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.web.mvc.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ProvidersController.
 * URI: /services/$service/providers
 *
 */
@Controller
@RequestMapping("/governance/addresses")
public class AddressesController extends BaseController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    ServicesController servicesController;

    private Map<String, Object> context = new HashMap<>();

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "addresses");
        List<String> providerAddresses = null;
        List<String> consumerAddresses = null;
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String application = (String)newModel.get("app");
        String service = (String)newModel.get("service");
        String address = (String)newModel.get("address");
        String keyword = (String)newModel.get("keyword");

        if (application != null && application.length() > 0) {
            providerAddresses = providerService.findAddressesByApplication(application);
            consumerAddresses = consumerService.findAddressesByApplication(application);
        } else if (service != null && service.length() > 0) {
            providerAddresses = providerService.findAddressesByService(service);
            consumerAddresses = consumerService.findAddressesByService(service);
        } else {
            providerAddresses = providerService.findAddresses();
            consumerAddresses = consumerService.findAddresses();
        }

        Set<String> addresses = new TreeSet<String>();
        if (providerAddresses != null) {
            addresses.addAll(providerAddresses);
        }
        if (consumerAddresses != null) {
            addresses.addAll(consumerAddresses);
        }
        model.addAttribute("providerAddresses", providerAddresses);
        model.addAttribute("consumerAddresses", consumerAddresses);
        model.addAttribute("addresses", addresses);

        if (service == null && application == null
                && address == null) {
            model.addAttribute("address", "*");
        }

        if (StringUtils.isNotEmpty(keyword)) {
            if ("*".equals(keyword)) {
                return "governance/screen/addresses/index";
            }

            keyword = keyword.toLowerCase();
            Set<String> newList = new HashSet<String>();
            Set<String> newProviders = new HashSet<String>();
            Set<String> newConsumers = new HashSet<String>();

            for (String o : addresses) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newList.add(o);
                }
            }
            for (String o : providerAddresses) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newProviders.add(o);
                }
            }
            for (String o : consumerAddresses) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newConsumers.add(o);
                }
            }
            context.put("addresses", newList);
            model.addAttribute("addresses", newList);
            model.addAttribute("providerAddresses", newProviders);
            model.addAttribute("consumerAddresses", newConsumers);
        }
        return "governance/screen/addresses/index";
    }

    //@RequestMapping("/{ip:[0-9.]+}/{type}")
    //public String addressMapping(@PathVariable("ip") String ip, @PathVariable("type") String type,
    //                             HttpServletRequest request, HttpServletResponse response, Model model) {
    //    return servicesController.route(null, type, null, ip, request, response, model);
    //}

    //public void search(
    //                   HttpServletRequest request, HttpServletResponse response, Model model) {
    //    index(request, response, model);
    //
    //    Set<String> newList = new HashSet<String>();
    //    @SuppressWarnings("unchecked")
    //    Set<String> list = (Set<String>) context.get("addresses");
    //    if (StringUtils.isNotEmpty(keyword)) {
    //        keyword = keyword.toLowerCase();
    //        for (String o : list) {
    //            if (o.toLowerCase().indexOf(keyword) != -1) {
    //                newList.add(o);
    //            }
    //        }
    //    }
    //    context.put("addresses", newList);
    //    model.addAttribute("addresses", newList);
    //}
}
