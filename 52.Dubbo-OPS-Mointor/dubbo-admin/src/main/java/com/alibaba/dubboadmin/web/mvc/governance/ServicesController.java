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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.OverrideService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.Override;
import com.alibaba.dubboadmin.registry.common.route.OverrideUtils;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ProvidersController. URI: /services/$service/providers /addresses/$address/services /application/$application/services
 *
 */
@Controller
@RequestMapping("/governance/services")
public class ServicesController extends BaseController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private OverrideService overrideService;


    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "services");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        String application = (String)newModel.get("app");
        String address = (String)newModel.get("address");
        String keyword = request.getParameter("keyword");

        if (service == null
                && application == null
                && address == null) {
            model.addAttribute("service", "*");
        }

        List<String> providerServices = null;
        List<String> consumerServices = null;
        List<Override> overrides = null;
        if (application != null && application.length() > 0) {
            model.addAttribute("app", application);
            providerServices = providerService.findServicesByApplication(application);
            consumerServices = consumerService.findServicesByApplication(application);
            overrides = overrideService.findByApplication(application);
        } else if (address != null && address.length() > 0) {
            providerServices = providerService.findServicesByAddress(address);
            consumerServices = consumerService.findServicesByAddress(address);
            overrides = overrideService.findByAddress(Tool.getIP(address));
        } else {
            providerServices = providerService.findServices();
            consumerServices = consumerService.findServices();
            overrides = overrideService.findAll();
        }

        Set<String> services = new TreeSet<String>();
        if (providerServices != null) {
            services.addAll(providerServices);
        }
        if (consumerServices != null) {
            services.addAll(consumerServices);
        }

        Map<String, List<Override>> service2Overrides = new HashMap<String, List<Override>>();
        if (overrides != null && overrides.size() > 0
                && services != null && services.size() > 0) {
            for (String s : services) {
                if (overrides != null && overrides.size() > 0) {
                    for (Override override : overrides) {
                        List<Override> serOverrides = new ArrayList<Override>();
                        if (override.isMatch(s, address, application)) {
                            serOverrides.add(override);
                        }
                        Collections.sort(serOverrides, OverrideUtils.OVERRIDE_COMPARATOR);
                        service2Overrides.put(s, serOverrides);
                    }
                }
            }
        }

        model.addAttribute("providerServices", providerServices);
        model.addAttribute("consumerServices", consumerServices);
        model.addAttribute("services", services);
        model.addAttribute("overrides", service2Overrides);


        if (keyword != null && !"*".equals(keyword)) {
            keyword = keyword.toLowerCase();
            Set<String> newList = new HashSet<String>();
            Set<String> newProviders = new HashSet<String>();
            Set<String> newConsumers = new HashSet<String>();

            for (String o : services) {
                if (o.toLowerCase().toLowerCase().indexOf(keyword) != -1) {
                    newList.add(o);
                }
                if (o.toLowerCase().toLowerCase().equals(keyword.toLowerCase())) {
                    service = o;
                }
            }
            for (String o : providerServices) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newProviders.add(o);
                }
            }
            for (String o : consumerServices) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newConsumers.add(o);
                }
            }
            model.addAttribute("services", newList);
            model.addAttribute("keyword", keyword);
            model.addAttribute("providerServices", newProviders);
            model.addAttribute("consumerServices", newConsumers);
        }
        return "governance/screen/services/index";
    }


    @RequestMapping("/{ids}/shield")
    public String shield(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return mock(ids, "force:return null", "shield", request, response, model);
    }

    @RequestMapping("/{ids}/tolerant")
    public String tolerant(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return mock(ids, "fail:return null", "tolerant", request, response, model);
    }

    @RequestMapping("/{ids}/recover")
    public String recover(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return mock(ids,  "", "recover", request, response, model);
    }

    private String mock(Long[] ids, String mock, String methodName, HttpServletRequest request,
                        HttpServletResponse response, Model model) throws Exception {
        prepare(request, response, model, methodName, "services");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String services = (String) newModel.get("service");
        String application = (String) newModel.get("app");

        if (services == null || services.length() == 0
                || application == null || application.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../services");
            return "governance/screen/redirect";
        }
        for (String service : SPACE_SPLIT_PATTERN.split(services)) {
            if (!super.currentUser.hasServicePrivilege(service)) {
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
                model.addAttribute("success", false);
                model.addAttribute("redirect", "../../services");
                return "governance/screen/redirect";
            }
        }
        for (String service : SPACE_SPLIT_PATTERN.split(services)) {
            List<Override> overrides = overrideService.findByServiceAndApplication(service, application);
            if (overrides != null && overrides.size() > 0) {
                for (Override override : overrides) {
                    Map<String, String> map = StringUtils.parseQueryString(override.getParams());
                    if (mock == null || mock.length() == 0) {
                        map.remove("mock");
                    } else {
                        map.put("mock", URL.encode(mock));
                    }
                    if (map.size() > 0) {
                        override.setParams(StringUtils.toQueryString(map));
                        override.setEnabled(true);
                        override.setOperator(operator);
                        override.setOperatorAddress(operatorAddress);
                        overrideService.updateOverride(override);
                    } else {
                        overrideService.deleteOverride(override.getId());
                    }
                }
            } else if (mock != null && mock.length() > 0) {
                Override override = new Override();
                override.setService(service);
                override.setApplication(application);
                override.setParams("mock=" + URL.encode(mock));
                override.setEnabled(true);
                override.setOperator(operator);
                override.setOperatorAddress(operatorAddress);
                overrideService.saveOverride(override);
            }
        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../services");
        return "governance/screen/redirect";
    }

}
