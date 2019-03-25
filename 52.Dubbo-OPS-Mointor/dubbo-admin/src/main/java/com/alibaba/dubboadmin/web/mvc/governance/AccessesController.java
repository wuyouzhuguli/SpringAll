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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.governance.service.RouteService;
import com.alibaba.dubboadmin.registry.common.domain.Access;
import com.alibaba.dubboadmin.registry.common.domain.Route;
import com.alibaba.dubboadmin.registry.common.route.RouteRule;
import com.alibaba.dubboadmin.registry.common.route.RouteRule.MatchPair;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ProvidersController. URI: /services/$service/accesses
 *
 */

@Controller
@RequestMapping("/governance/accesses")
public class AccessesController extends BaseController {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}$");
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
    private static final Pattern ALL_IP_PATTERN = Pattern.compile("0{1,3}(\\.0{1,3}){3}$");

    @Autowired
    private RouteService routeService;
    @Autowired
    private ProviderService providerService;

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "accesses");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String address = (String)newModel.get("address");
        String service = (String)newModel.get("service");

        address = Tool.getIP(address);
        List<Route> routes;
        if (service != null && service.length() > 0) {
            routes = routeService.findForceRouteByService(service);
        } else if (address != null && address.length() > 0) {
            routes = routeService.findForceRouteByAddress(address);
        } else {
            routes = routeService.findAllForceRoute();
        }
        List<Access> accesses = new ArrayList<Access>();
        if (routes == null) {
            model.addAttribute("accesses", accesses);
            return "governance/screen/accesses/index";
        }
        for (Route route : routes) {
            Map<String, MatchPair> rule = null;
            try {
                rule = RouteRule.parseRule(route.getMatchRule());
            } catch (ParseException e) {
                logger.error("parse rule error", e);
            }
            MatchPair pair = rule.get("consumer.host");
            if (pair != null) {
                for (String host : pair.getMatches()) {
                    Access access = new Access();
                    access.setAddress(host);
                    access.setService(route.getService());
                    access.setAllow(false);
                    accesses.add(access);
                }
                for (String host : pair.getUnmatches()) {
                    Access access = new Access();
                    access.setAddress(host);
                    access.setService(route.getService());
                    access.setAllow(true);
                    accesses.add(access);
                }
            }
        }
        model.addAttribute("accesses", accesses);
        return "governance/screen/accesses/index";
    }

    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "add", "accesses");
        List<String> serviceList = Tool.sortSimpleName(providerService.findServices());
        model.addAttribute("serviceList", serviceList);
        return "governance/screen/accesses/add";
    }

    @RequestMapping("/create")
    public String create(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        prepare(request, response, model, "create", "accesses");
        String addr = request.getParameter("consumerAddress");
        String services = request.getParameter("service");
        Set<String> consumerAddresses = toAddr(addr);
        Set<String> aimServices = toService(services);
        for (String aimService : aimServices) {
            boolean isFirst = false;
            List<Route> routes = routeService.findForceRouteByService(aimService);
            Route route = null;
            if (routes == null || routes.size() == 0) {
                isFirst = true;
                route = new Route();
                route.setService(aimService);
                route.setForce(true);
                route.setName(aimService + " blackwhitelist");
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
            for (String consumerAddress : consumerAddresses) {
                if (Boolean.valueOf((String) request.getParameter("allow"))) {
                    matchPair.getUnmatches().add(Tool.getIP(consumerAddress));

                } else {
                    matchPair.getMatches().add(Tool.getIP(consumerAddress));
                }
            }
            StringBuilder sb = new StringBuilder();
            RouteRule.contidionToString(sb, when);
            route.setMatchRule(sb.toString());
            route.setUsername(operator);
            if (isFirst) {
                routeService.createRoute(route);
            } else {
                routeService.updateRoute(route);
            }

        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../accesses");
        return "governance/screen/redirect";

    }

    private Set<String> toAddr(String addr) throws IOException {
        Set<String> consumerAddresses = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new StringReader(addr));
        while (true) {
            String line = reader.readLine();
            if (null == line)
                break;

            String[] split = line.split("[\\s,;]+");
            for (String s : split) {
                if (s.length() == 0)
                    continue;
                if (!IP_PATTERN.matcher(s).matches()) {
                    throw new IllegalStateException("illegal IP: " + s);
                }
                if (LOCAL_IP_PATTERN.matcher(s).matches() || ALL_IP_PATTERN.matcher(s).matches()) {
                    throw new IllegalStateException("local IP or any host ip is illegal: " + s);
                }

                consumerAddresses.add(s);
            }
        }
        return consumerAddresses;
    }

    private Set<String> toService(String services) throws IOException {
        Set<String> aimServices = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new StringReader(services));
        while (true) {
            String line = reader.readLine();
            if (null == line)
                break;

            String[] split = line.split("[\\s,;]+");
            for (String s : split) {
                if (s.length() == 0)
                    continue;
                aimServices.add(s);
            }
        }
        return aimServices;
    }

    /**
     *
     * @throws ParseException
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam String accesses, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        prepare(request, response, model, "delete", "accesses");
        String[] temp = accesses.split(" ");
        Map<String, Set<String>> prepareToDeleate = new HashMap<String, Set<String>>();
        for (String s : temp) {
            String service = s.split("=")[0];
            String address = s.split("=")[1];
            Set<String> addresses = prepareToDeleate.get(service);
            if (addresses == null) {
                prepareToDeleate.put(service, new HashSet<String>());
                addresses = prepareToDeleate.get(service);
            }
            addresses.add(address);
        }
        for (Entry<String, Set<String>> entry : prepareToDeleate.entrySet()) {

            String service = entry.getKey();
            List<Route> routes = routeService.findForceRouteByService(service);
            if (routes == null || routes.size() == 0) {
                continue;
            }
            for (Route blackwhitelist : routes) {
                MatchPair pairs = RouteRule.parseRule(blackwhitelist.getMatchRule()).get("consumer.host");
                Set<String> matches = new HashSet<String>();
                matches.addAll(pairs.getMatches());
                Set<String> unmatches = new HashSet<String>();
                unmatches.addAll(pairs.getUnmatches());
                for (String pair : pairs.getMatches()) {
                    for (String address : entry.getValue()) {
                        if (pair.equals(address)) {
                            matches.remove(pair);
                            break;
                        }
                    }
                }
                for (String pair : pairs.getUnmatches()) {
                    for (String address : entry.getValue()) {
                        if (pair.equals(address)) {
                            unmatches.remove(pair);
                            break;
                        }
                    }
                }
                if (matches.size() == 0 && unmatches.size() == 0) {
                    routeService.deleteRoute(blackwhitelist.getId());
                } else {
                    Map<String, MatchPair> condition = new HashMap<String, MatchPair>();
                    condition.put("consumer.host", new MatchPair(matches, unmatches));
                    StringBuilder sb = new StringBuilder();
                    RouteRule.contidionToString(sb, condition);
                    blackwhitelist.setMatchRule(sb.toString());
                    routeService.updateRoute(blackwhitelist);
                }
            }

        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../accesses");
        return "governance/screen/redirect";
    }

    public void show(Map<String, Object> context) {
    }

    public void edit(Map<String, Object> context) {
    }

    public String update(Map<String, Object> context) {
        return null;
    }

}
