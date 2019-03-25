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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.OverrideService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.governance.service.RouteService;
import com.alibaba.dubboadmin.registry.common.domain.Consumer;
import com.alibaba.dubboadmin.registry.common.domain.Override;
import com.alibaba.dubboadmin.registry.common.domain.Provider;
import com.alibaba.dubboadmin.registry.common.domain.Route;
import com.alibaba.dubboadmin.registry.common.route.OverrideUtils;
import com.alibaba.dubboadmin.registry.common.route.RouteRule;
import com.alibaba.dubboadmin.registry.common.route.RouteRule.MatchPair;
import com.alibaba.dubboadmin.registry.common.route.RouteUtils;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ConsumersController. URI: /services/$service/consumers
 *
 */
@Controller
@RequestMapping("/governance/consumers")
public class ConsumersController extends BaseController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private OverrideService overrideService;

    @Autowired
    private RouteService routeService;

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response,
                      Model model) {
        prepare(request, response, model, "index", "consumers");
        List<Consumer> consumers;
        List<Override> overrides;
        List<Provider> providers = null;
        List<Route> routes = null;
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        String address = (String)newModel.get("address");
        String application = (String)newModel.get("app");
        // service
        if (service != null && service.length() > 0) {
            consumers = consumerService.findByService(service);
            overrides = overrideService.findByService(service);
            providers = providerService.findByService(service);
            routes = routeService.findByService(service);
        }
        // address
        else if (address != null && address.length() > 0) {
            consumers = consumerService.findByAddress(address);
            overrides = overrideService.findByAddress(Tool.getIP(address));
        }
        // application
        else if (application != null && application.length() > 0) {
            consumers = consumerService.findByApplication(application);
            overrides = overrideService.findByApplication(application);
        }
        // all
        else {
            consumers = consumerService.findAll();
            overrides = overrideService.findAll();
        }
        if (consumers != null && consumers.size() > 0) {
            for (Consumer consumer : consumers) {
                if (service == null || service.length() == 0) {
                    providers = providerService.findByService(consumer.getService());
                    routes = routeService.findByService(consumer.getService());
                }
                List<Route> routed = new ArrayList<Route>();
                consumer.setProviders(RouteUtils
                    .route(consumer.getService(), consumer.getAddress(), consumer.getParameters(), providers, overrides, routes, null, routed));
                consumer.setRoutes(routed);
                OverrideUtils.setConsumerOverrides(consumer, overrides);
            }
        }
        model.addAttribute("consumers", consumers);
        return "governance/screen/consumers/index";
    }

    @RequestMapping("/{id}")
    public String show(@PathVariable("id") Long id,
                       HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "show", "consumers");
        Consumer consumer = consumerService.findConsumer(id);
        List<Provider> providers = providerService.findByService(consumer.getService());
        List<Route> routes = routeService.findByService(consumer.getService());
        List<Override> overrides = overrideService.findByService(consumer.getService());
        List<Route> routed = new ArrayList<Route>();
        consumer.setProviders(RouteUtils.route(consumer.getService(), consumer.getAddress(), consumer.getParameters(), providers, overrides, routes, null, routed));
        consumer.setRoutes(routed);
        OverrideUtils.setConsumerOverrides(consumer, overrides);
        model.addAttribute("consumer", consumer);
        model.addAttribute("providers", consumer.getProviders());
        model.addAttribute("routes", consumer.getRoutes());
        model.addAttribute("overrides", consumer.getOverrides());
        return "governance/screen/consumers/show";
    }

    @RequestMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response,  Model model) {
        prepare(request, response, model, "edit", "consumers");
        Consumer consumer = consumerService.findConsumer(id);
        List<Provider> providers = providerService.findByService(consumer.getService());
        List<Route> routes = routeService.findByService(consumer.getService());
        List<Override> overrides = overrideService.findByService(consumer.getService());
        List<Route> routed = new ArrayList<Route>();
        consumer.setProviders(RouteUtils.route(consumer.getService(), consumer.getAddress(), consumer.getParameters(), providers, overrides, routes, null, routed));
        consumer.setRoutes(routed);
        OverrideUtils.setConsumerOverrides(consumer, overrides);
        model.addAttribute("consumer", consumer);
        model.addAttribute("providers", consumer.getProviders());
        model.addAttribute("routes", consumer.getRoutes());
        model.addAttribute("overrides", consumer.getOverrides());
        return "governance/screen/consumers/edit";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST) //post
    public String update(@ModelAttribute Consumer newConsumer, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "consumers");
        boolean success = true;
        Long id = newConsumer.getId();
        String parameters = newConsumer.getParameters();
        Consumer consumer = consumerService.findConsumer(id);
        if (consumer == null) {
            model.addAttribute("message", getMessage("NoSuchOperationData", id));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "governance/consumers");
            return "governance/screen/redirect";
        }
        String service = consumer.getService();
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "governance/consumers");
            return "governance/screen/redirect";
        }
        Map<String, String> oldMap = StringUtils.parseQueryString(consumer.getParameters());
        Map<String, String> newMap = StringUtils.parseQueryString(parameters);
        for (Map.Entry<String, String> entry : oldMap.entrySet()) {
            if (entry.getValue().equals(newMap.get(entry.getKey()))) {
                newMap.remove(entry.getKey());
            }
        }
        String address = consumer.getAddress();
        List<Override> overrides = overrideService.findByServiceAndAddress(consumer.getService(), consumer.getAddress());
        OverrideUtils.setConsumerOverrides(consumer, overrides);
        Override override = consumer.getOverride();
        if (override != null) {
            if (newMap.size() > 0) {
                override.setParams(StringUtils.toQueryString(newMap));
                override.setEnabled(true);
                override.setOperator(operator);
                override.setOperatorAddress(operatorAddress);
                overrideService.updateOverride(override);
            } else {
                overrideService.deleteOverride(override.getId());
            }
        } else {
            override = new Override();
            override.setService(service);
            override.setAddress(address);
            override.setParams(StringUtils.toQueryString(newMap));
            override.setEnabled(true);
            override.setOperator(operator);
            override.setOperatorAddress(operatorAddress);
            overrideService.saveOverride(override);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "governance/consumers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{id}/routed")
    public String routed(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "routed", "consumers");
        showDetail(id, request, response, model);
        return "governance/screen/consumers/routed";
    }

    @RequestMapping("/{id}/notified")
    public String notified(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "notified", "consumers");
        showDetail(id, request, response, model);
        return "governance/screen/consumers/notified";
    }

    @RequestMapping("/{id}/overrided")
    public String overrided(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "overrided", "consumers");
        showDetail(id, request, response, model);
        return "governance/screen/consumers/overrided";
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
        prepare(request, response, model, methodName, "consumers");
        boolean success = true;
        if (ids == null || ids.length == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../../consumers");
            return "governance/screen/redirect";
        }
        List<Consumer> consumers = new ArrayList<Consumer>();
        for (Long id : ids) {
            Consumer c = consumerService.findConsumer(id);
            if (c != null) {
                consumers.add(c);
                if (!super.currentUser.hasServicePrivilege(c.getService())) {
                    model.addAttribute("message", getMessage("HaveNoServicePrivilege", c.getService()));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../consumers");
                    return "governance/screen/redirect";
                }
            }
        }
        for (Consumer consumer : consumers) {
            String service = consumer.getService();
            String address = Tool.getIP(consumer.getAddress());
            List<Override> overrides = overrideService.findByServiceAndAddress(service, address);
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
                override.setAddress(address);
                override.setParams("mock=" + URL.encode(mock));
                override.setEnabled(true);
                override.setOperator(operator);
                override.setOperatorAddress(operatorAddress);
                overrideService.saveOverride(override);
            }
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../consumers");
        return "governance/screen/redirect";
    }

    private void showDetail( Long id,
                             HttpServletRequest request, HttpServletResponse response, Model model) {
        Consumer consumer = consumerService.findConsumer(id);
        List<Provider> providers = providerService.findByService(consumer.getService());
        List<Route> routes = routeService.findByService(consumer.getService());
        List<Override> overrides = overrideService.findByService(consumer.getService());
        List<Route> routed = new ArrayList<Route>();
        consumer.setProviders(RouteUtils.route(consumer.getService(), consumer.getAddress(), consumer.getParameters(), providers, overrides, routes, null, routed));
        consumer.setRoutes(routed);
        OverrideUtils.setConsumerOverrides(consumer, overrides);
        model.addAttribute("consumer", consumer);
        model.addAttribute("providers", consumer.getProviders());
        model.addAttribute("routes", consumer.getRoutes());
        model.addAttribute("overrides", consumer.getOverrides());
    }

    @RequestMapping("/allshield")
    public String allshield(@RequestParam(required = false) String service, HttpServletRequest request,
                                                   HttpServletResponse response, Model model) throws Exception {
        return allmock(service,  "force:return null", "allshield",request, response, model);
    }

    @RequestMapping("/alltolerant")
    public String alltolerant(@RequestParam(required = false) String service, HttpServletRequest request,
                               HttpServletResponse response, Model model) throws Exception {
        return allmock(service, "fail:return null", "alltolerant", request, response, model);
    }

    @RequestMapping("/allrecover")
    public String allrecover(@RequestParam(required = false) String service, HttpServletRequest request,
                              HttpServletResponse response, Model model) throws Exception {
        return allmock(service, "", "allrecover", request, response, model);
    }

    private String allmock(String service, String mock, String methodName, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        prepare(request, response, model, methodName,"consumers");
        boolean success = true;
        if (service == null || service.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../consumers");
            return "governance/screen/redirect";
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../consumers");
            return "governance/screen/redirect";
        }
        List<Override> overrides = overrideService.findByService(service);
        Override allOverride = null;
        if (overrides != null && overrides.size() > 0) {
            for (Override override : overrides) {
                if (override.isDefault()) {
                    allOverride = override;
                    break;
                }
            }
        }
        if (allOverride != null) {
            Map<String, String> map = StringUtils.parseQueryString(allOverride.getParams());
            if (mock == null || mock.length() == 0) {
                map.remove("mock");
            } else {
                map.put("mock", URL.encode(mock));
            }
            if (map.size() > 0) {
                allOverride.setParams(StringUtils.toQueryString(map));
                allOverride.setEnabled(true);
                allOverride.setOperator(operator);
                allOverride.setOperatorAddress(operatorAddress);
                overrideService.updateOverride(allOverride);
            } else {
                overrideService.deleteOverride(allOverride.getId());
            }
        } else if (mock != null && mock.length() > 0) {
            Override override = new Override();
            override.setService(service);
            override.setParams("mock=" + URL.encode(mock));
            override.setEnabled(true);
            override.setOperator(operator);
            override.setOperatorAddress(operatorAddress);
            overrideService.saveOverride(override);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../consumers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/allow")
    public String allow(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return access(request, response, ids, model, true, false, "allow");
    }

    @RequestMapping("/{ids}/forbid")
    public String forbid(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return access(request, response, ids, model, false, false, "forbid");
    }

    @RequestMapping("/{ids}/onlyallow")
    public String onlyallow(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return access(request, response, ids, model, true, true, "onlyallow");
    }

    @RequestMapping("/{ids}/onlyforbid")
    public String onlyforbid(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        return access(request, response, ids, model, false, true, "onlyforbid");
    }

    private String access(HttpServletRequest request, HttpServletResponse response, Long[] ids,
                           Model model, boolean allow, boolean only, String methodName) throws Exception {
        prepare(request, response, model, methodName, "consumers");
        boolean success = true;
        if (ids == null || ids.length == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../../consumers");
            return "governance/screen/redirect";
        }
        List<Consumer> consumers = new ArrayList<Consumer>();
        for (Long id : ids) {
            Consumer c = consumerService.findConsumer(id);
            if (c != null) {
                consumers.add(c);
                if (!super.currentUser.hasServicePrivilege(c.getService())) {
                    model.addAttribute("message", getMessage("HaveNoServicePrivilege", c.getService()));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../consumers");
                    return "governance/screen/redirect";
                }
            }
        }
        Map<String, Set<String>> serviceAddresses = new HashMap<String, Set<String>>();
        for (Consumer consumer : consumers) {
            String service = consumer.getService();
            String address = Tool.getIP(consumer.getAddress());
            Set<String> addresses = serviceAddresses.get(service);
            if (addresses == null) {
                addresses = new HashSet<String>();
                serviceAddresses.put(service, addresses);
            }
            addresses.add(address);
        }
        for (Map.Entry<String, Set<String>> entry : serviceAddresses.entrySet()) {
            String service = entry.getKey();
            boolean isFirst = false;
            List<Route> routes = routeService.findForceRouteByService(service);
            Route route = null;
            if (routes == null || routes.size() == 0) {
                isFirst = true;
                route = new Route();
                route.setService(service);
                route.setForce(true);
                route.setName(service + " blackwhitelist");
                route.setFilterRule("false");
                route.setEnabled(true);
            } else {
                route = routes.get(0);
            }
            Map<String, MatchPair> when = null;
            MatchPair matchPair = null;
            if (isFirst) {
                when = new HashMap<String, MatchPair>();
                matchPair = new MatchPair(new HashSet<String>(), new HashSet<String>());
                when.put("consumer.host", matchPair);
            } else {
                when = RouteRule.parseRule(route.getMatchRule());
                matchPair = when.get("consumer.host");
            }
            if (only) {
                matchPair.getUnmatches().clear();
                matchPair.getMatches().clear();
                if (allow) {
                    matchPair.getUnmatches().addAll(entry.getValue());
                } else {
                    matchPair.getMatches().addAll(entry.getValue());
                }
            } else {
                for (String consumerAddress : entry.getValue()) {
                    if (matchPair.getUnmatches().size() > 0) { // whitelist take effect
                        matchPair.getMatches().remove(consumerAddress); // remove data in blacklist
                        if (allow) { // if allowed
                            matchPair.getUnmatches().add(consumerAddress); // add to whitelist
                        } else { // if not allowed
                            matchPair.getUnmatches().remove(consumerAddress); // remove from whitelist
                        }
                    } else { // blacklist take effect
                        if (allow) { // if allowed
                            matchPair.getMatches().remove(consumerAddress); // remove from blacklist
                        } else { // if not allowed
                            matchPair.getMatches().add(consumerAddress); // add to blacklist
                        }
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            RouteRule.contidionToString(sb, when);
            route.setMatchRule(sb.toString());
            route.setUsername(operator);
            if (matchPair.getMatches().size() > 0 || matchPair.getUnmatches().size() > 0) {
                if (isFirst) {
                    routeService.createRoute(route);
                } else {
                    routeService.updateRoute(route);
                }
            } else if (!isFirst) {
                routeService.deleteRoute(route.getId());
            }
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../consumers");
        return "governance/screen/redirect";
    }
}
