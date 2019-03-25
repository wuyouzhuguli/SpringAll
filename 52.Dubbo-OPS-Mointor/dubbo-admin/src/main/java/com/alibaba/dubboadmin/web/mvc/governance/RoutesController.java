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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.OwnerService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.governance.service.RouteService;
import com.alibaba.dubboadmin.registry.common.domain.Consumer;
import com.alibaba.dubboadmin.registry.common.domain.Provider;
import com.alibaba.dubboadmin.registry.common.domain.Route;
import com.alibaba.dubboadmin.registry.common.route.ParseUtils;
import com.alibaba.dubboadmin.registry.common.route.RouteRule;
import com.alibaba.dubboadmin.registry.common.route.RouteUtils;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ProvidersController.
 * URI: /services/$service/routes
 *
 */
@Controller
@RequestMapping("/governance/routes")
public class RoutesController extends BaseController {

    private static final int MAX_RULE_LENGTH = 1000;
    static String[][] when_names = {
            {"method", "method", "unmethod"},
            {"consumer.application", "consumerApplication", "unconsumerApplication"},
            {"consumer.cluster", "consumerCluster", "unconsumerCluster"},
            {"consumer.host", "consumerHost", "unconsumerHost"},
            {"consumer.version", "consumerVersion", "unconsumerVersion"},
            {"consumer.group", "consumerGroup", "unconsumerGroup"},
    };
    static String[][] then_names = {
            {"provider.application", "providerApplication", "unproviderApplication"},
            {"provider.cluster", "providerCluster", "unproviderCluster"}, // Must check if Cluster exists
            {"provider.host", "providerHost", "unproviderHost"},
            {"provider.protocol", "providerProtocol", "unproviderProtocol"},
            {"provider.port", "providerPort", "unproviderPort"},
            {"provider.version", "providerVersion", "unproviderVersion"},
            {"provider.group", "providerGroup", "unproviderGroup"}
    };
    @Autowired
    private RouteService routeService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ConsumerService consumerService;

    static void checkService(String service) {
        if (service.contains(",")) throw new IllegalStateException("service(" + service + ") contain illegale ','");

        String interfaceName = service;
        int gi = interfaceName.indexOf("/");
        if (gi != -1) interfaceName = interfaceName.substring(gi + 1);
        int vi = interfaceName.indexOf(':');
        if (vi != -1) interfaceName = interfaceName.substring(0, vi);

        if (interfaceName.indexOf('*') != -1 && interfaceName.indexOf('*') != interfaceName.length() - 1) {
            throw new IllegalStateException("service(" + service + ") only allow 1 *, and must be last char!");
        }
    }

    /**
     * add owners related with service
     *
     * @param usernames   the usernames to add
     * @param serviceName no wildcards
     */
    public static void addOwnersOfService(Set<String> usernames, String serviceName,
                                          OwnerService ownerDAO) {
        List<String> serviceNamePatterns = ownerDAO.findAllServiceNames();
        for (String p : serviceNamePatterns) {
            if (ParseUtils.isMatchGlobPattern(p, serviceName)) {
                List<String> list = ownerDAO.findUsernamesByServiceName(p);
                usernames.addAll(list);
            }
        }
    }

    /**
     * add owners related with service pattern
     *
     * @param usernames          the usernames to add
     * @param serviceNamePattern service pattern, Glob
     */
    public static void addOwnersOfServicePattern(Set<String> usernames, String serviceNamePattern,
                                                 OwnerService ownerDAO) {
        List<String> serviceNamePatterns = ownerDAO.findAllServiceNames();
        for (String p : serviceNamePatterns) {
            if (ParseUtils.hasIntersection(p, serviceNamePattern)) {
                List<String> list = ownerDAO.findUsernamesByServiceName(p);
                usernames.addAll(list);
            }
        }
    }

    /**
     * Routing module home page
     *
     */
    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "routes");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String address = (String)newModel.get("address");
        String service = (String)newModel.get("service");
        address = Tool.getIP(address);
        List<Route> routes;
        if (service != null && service.length() > 0
                && address != null && address.length() > 0) {
            routes = routeService.findByServiceAndAddress(service, address);
        } else if (service != null && service.length() > 0) {
            routes = routeService.findByService(service);
        } else if (address != null && address.length() > 0) {
            routes = routeService.findByAddress(address);
        } else {
            routes = routeService.findAll();
        }
        model.addAttribute("routes", routes);
        return "governance/screen/routes/index";
    }

    /**
     * Display routing details
     *
     */
    @RequestMapping("/{id}")
    public String show(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            prepare(request, response, model, "show", "routes");
            Route route = routeService.findRoute(id);

            if (route == null) {
                throw new IllegalArgumentException("The route is not existed.");
            }
            if (route.getService() != null && !route.getService().isEmpty()) {
                model.addAttribute("service", route.getService());
            }

            RouteRule routeRule = RouteRule.parse(route);

            @SuppressWarnings("unchecked")
            Map<String, RouteRule.MatchPair>[] paramArray = new Map[]{
                    routeRule.getWhenCondition(), routeRule.getThenCondition()};
            String[][][] namesArray = new String[][][]{when_names, then_names};

            for (int i = 0; i < paramArray.length; ++i) {
                Map<String, RouteRule.MatchPair> param = paramArray[i];
                String[][] names = namesArray[i];
                for (String[] name : names) {
                    RouteRule.MatchPair matchPair = param.get(name[0]);
                    if (matchPair == null) {
                        continue;
                    }

                    if (!matchPair.getMatches().isEmpty()) {
                        String m = RouteRule.join(matchPair.getMatches());
                        model.addAttribute(name[1], m);
                    }
                    if (!matchPair.getUnmatches().isEmpty()) {
                        String u = RouteRule.join(matchPair.getUnmatches());
                        model.addAttribute(name[2], u);
                    }
                }
            }
            model.addAttribute("route", route);
            model.addAttribute("methods", CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(route.getService()))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "governance/screen/routes/show";
    }

    /**
     * Load new route page
     *
     */
    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "add", "routes");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        if (service != null && service.length() > 0 && !service.contains("*")) {
            model.addAttribute("service", service);
            model.addAttribute("methods", CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(service))));
        } else {
            List<String> serviceList = Tool.sortSimpleName(new ArrayList<String>(providerService.findServices()));
            model.addAttribute("serviceList", serviceList);
        }

        //if (input != null) model.addAttribute("input", input);
        return "governance/screen/routes/add";
    }

    /**
     * Load modified routing page
     *
     */

    @RequestMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, @RequestParam(required = false) String service,
                     @RequestParam(required = false) String input,
                     HttpServletRequest request, HttpServletResponse response, Model model) {

        prepare(request, response, model, "edit", "routes");
        if (service != null && service.length() > 0 && !service.contains("*")) {
            model.addAttribute("service", service);
            model.addAttribute("methods", CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(service))));
        } else {
            List<String> serviceList = Tool.sortSimpleName(new ArrayList<String>(providerService.findServices()));
            model.addAttribute("serviceList", serviceList);
        }

        if (input != null) model.addAttribute("input", input);
        Route route = routeService.findRoute(id);

        if (route == null) {
            throw new IllegalArgumentException("The route is not existed.");
        }
        if (route.getService() != null && !route.getService().isEmpty()) {
            model.addAttribute("service", route.getService());
        }

        RouteRule routeRule = null;
        try {
            routeRule = RouteRule.parse(route);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Map<String, RouteRule.MatchPair>[] paramArray = new Map[]{
            routeRule.getWhenCondition(), routeRule.getThenCondition()};
        String[][][] namesArray = new String[][][]{when_names, then_names};

        for (int i = 0; i < paramArray.length; ++i) {
            Map<String, RouteRule.MatchPair> param = paramArray[i];
            String[][] names = namesArray[i];
            for (String[] name : names) {
                RouteRule.MatchPair matchPair = param.get(name[0]);
                if (matchPair == null) {
                    continue;
                }

                if (!matchPair.getMatches().isEmpty()) {
                    String m = RouteRule.join(matchPair.getMatches());
                    model.addAttribute(name[1], m);
                }
                if (!matchPair.getUnmatches().isEmpty()) {
                    String u = RouteRule.join(matchPair.getUnmatches());
                    model.addAttribute(name[2], u);
                }
            }
        }
        model.addAttribute("route", route);
        model.addAttribute("methods", CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(route.getService()))));

        return "governance/screen/routes/edit";
    }

    /**
     * Save the routing information to the database
     *
     * @return
     */

    @RequestMapping("/create")
    public String create(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "routes");
        boolean success = true;

        String name = request.getParameter("name");
        String service = request.getParameter("service");
        if (StringUtils.isNotEmpty(service)
                && StringUtils.isNotEmpty(name)) {
            checkService(service);

            Map<String, String> when_name2valueList = new HashMap<String, String>();
            Map<String, String> notWhen_name2valueList = new HashMap<String, String>();
            for (String[] names : when_names) {
                when_name2valueList.put(names[0],  request.getParameter(names[1]));
                notWhen_name2valueList.put(names[0], request.getParameter(names[2])); // TODO. We should guarantee value is never null in here, will be supported later
            }

            Map<String, String> then_name2valueList = new HashMap<String, String>();
            Map<String, String> notThen_name2valueList = new HashMap<String, String>();
            for (String[] names : then_names) {
                then_name2valueList.put(names[0], request.getParameter(names[1]));
                notThen_name2valueList.put(names[0], request.getParameter(names[2]));
            }

            RouteRule routeRule = RouteRule.createFromNameAndValueListString(
                    when_name2valueList, notWhen_name2valueList,
                    then_name2valueList, notThen_name2valueList);

            if (routeRule.getThenCondition().isEmpty()) {
                model.addAttribute("message", getMessage("Add route error! then is empty."));
                model.addAttribute("success", false);
                model.addAttribute("redirect", "../routes");
                return "governance/screen/redirect";
            }

            String matchRule = routeRule.getWhenConditionString();
            String filterRule = routeRule.getThenConditionString();

            // Limit the length of the expression
            if (matchRule.length() > MAX_RULE_LENGTH) {
                model.addAttribute("message", getMessage("When rule is too long!"));
                model.addAttribute("success", false);
                model.addAttribute("redirect", "../routes");
                return "governance/screen/redirect";
            }
            if (filterRule.length() > MAX_RULE_LENGTH) {
                model.addAttribute("message", getMessage("Then rule is too long!"));
                model.addAttribute("success", false);
                model.addAttribute("redirect", "../routes");
                return "governance/screen/redirect";
            }

            Route route = new Route();
            route.setService(service);
            route.setName(name);
            route.setUsername(request.getParameter("operator"));
            route.setOperator(request.getParameter("operatorAddress"));
            route.setRule(routeRule.toString());
            if (StringUtils.isNotEmpty(request.getParameter("priority"))) {
                route.setPriority(Integer.parseInt(request.getParameter("priority")));
            }
            routeService.createRoute(route);

        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../routes");
        return "governance/screen/redirect";
    }

    /**
     * Save the update data to the database
     *
     * @return
     */
    @RequestMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "routes");
        boolean success = true;
        String idStr = String.valueOf(id);
        if (idStr != null && idStr.length() > 0) {
            String[] blacks = request.getParameterMap().get("black");
            //String[] blacks = (String[]) context.get("black");
            boolean black = false;
            if (blacks != null && blacks.length > 0) {
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../routes");
                return "governance/screen/redirect";
            }

            Route oldRoute = routeService.findRoute(Long.valueOf(idStr));
            if (null == oldRoute) {
                model.addAttribute("message", getMessage("NoSuchRecord"));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../routes");
                return "governance/screen/redirect";
            }
            // Check parameters, patchwork rule
            if (StringUtils.isNotEmpty((String) request.getParameter("name"))) {
                String service = oldRoute.getService();
                if (((BindingAwareModelMap)model).get("operator") == null) {
                    model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../routes");
                    return "governance/screen/redirect";
                }

                Map<String, String> when_name2valueList = new HashMap<String, String>();
                Map<String, String> notWhen_name2valueList = new HashMap<String, String>();
                for (String[] names : when_names) {
                    when_name2valueList.put(names[0], (String) request.getParameter(names[1]));
                    notWhen_name2valueList.put(names[0], (String) request.getParameter(names[2]));
                }

                Map<String, String> then_name2valueList = new HashMap<String, String>();
                Map<String, String> notThen_name2valueList = new HashMap<String, String>();
                for (String[] names : then_names) {
                    then_name2valueList.put(names[0], (String) request.getParameter(names[1]));
                    notThen_name2valueList.put(names[0], (String) request.getParameter(names[2]));
                }

                RouteRule routeRule = RouteRule.createFromNameAndValueListString(
                        when_name2valueList, notWhen_name2valueList,
                        then_name2valueList, notThen_name2valueList);

                RouteRule result = null;
                if (black) {
                    RouteRule.MatchPair matchPair = routeRule.getThenCondition().get("black");
                    Map<String, RouteRule.MatchPair> then = null;
                    if (null == matchPair) {
                        matchPair = new RouteRule.MatchPair();
                        then = new HashMap<String, RouteRule.MatchPair>();
                        then.put("black", matchPair);
                    } else {
                        matchPair.getMatches().clear();
                    }
                    matchPair.getMatches().add(String.valueOf(black));
                    result = RouteRule.copyWithReplace(routeRule, null, then);
                }

                if (result == null) {
                    result = routeRule;
                }

                if (result.getThenCondition().isEmpty()) {
                    model.addAttribute("message", getMessage("Update route error! then is empty."));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../routes");
                    return "governance/screen/redirect";
                }

                String matchRule = result.getWhenConditionString();
                String filterRule = result.getThenConditionString();

                // Limit the length of the expression
                if (matchRule.length() > MAX_RULE_LENGTH) {
                    model.addAttribute("message", getMessage("When rule is too long!"));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../routes");
                    return "governance/screen/redirect";
                }
                if (filterRule.length() > MAX_RULE_LENGTH) {
                    model.addAttribute("message", getMessage("Then rule is too long!"));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../routes");
                    return "governance/screen/redirect";
                }

                int priority = 0;
                if (StringUtils.isNotEmpty((String) request.getParameter("priority"))) {
                    priority = Integer.parseInt((String) request.getParameter("priority"));
                }

                Route route = new Route();
                route.setRule(result.toString());
                route.setService(service);
                route.setPriority(priority);
                route.setName((String) request.getParameter("name"));
                route.setUsername((String) request.getParameter("operator"));
                route.setOperator((String) request.getParameter("operatorAddress"));
                route.setId(Long.valueOf(idStr));
                route.setPriority(Integer.parseInt((String) request.getParameter("priority")));
                route.setEnabled(oldRoute.isEnabled());
                routeService.updateRoute(route);

                Set<String> usernames = new HashSet<String>();
                usernames.add((String) request.getParameter("operator"));
                usernames.add(route.getUsername());
                //RelateUserUtils.addOwnersOfService(usernames, route.getService(), ownerDAO);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("action", "update");
                params.put("route", route);

            } else {
                model.addAttribute("message", getMessage("MissRequestParameters", "name"));
            }
        } else {
            model.addAttribute("message", getMessage("MissRequestParameters", "id"));
        }

        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../routes");
        return "governance/screen/redirect";
    }

    /**
     * Remove the route rule for the specified ID
     *
     * @param ids
     * @return
     */
    @RequestMapping("/{ids}/delete")
    public String delete(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response,
                          Model model) {
        prepare(request, response, model, "delete", "routes");
        for (Long id : ids) {
            routeService.deleteRoute(id);
        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../routes");
        return "governance/screen/redirect";

    }

    /**
     * Enable the specified route ID rules (batch processing)
     *
     * @param ids
     * @return
     */
    @RequestMapping("/{ids}/enable")
    public String enable(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response,
                          Model model) {
        prepare(request, response, model, "enable", "routes");
        for (Long id : ids) {
            routeService.enableRoute(id);
        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../routes");
        return "governance/screen/redirect";
    }

    /**
     * Disabling route rules for specified IDs (can be batch processed)
     *
     * @param ids
     * @return
     */
    @RequestMapping("/{ids}/disable")
    public String disable(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response,
                           Model model) {
        prepare(request, response, model, "disable", "routes");
        for (Long id : ids) {
            routeService.disableRoute(id);
        }
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../routes");
        return "governance/screen/redirect";
    }

    /**
     * Choose consumers
     *
     * @param context
     */
    public void routeselect(Map<String, Object> context) {
        long rid = Long.valueOf((String) context.get("id"));
        context.put("id", rid);

        Route route = routeService.findRoute(rid);
        if (route == null) {
            throw new IllegalStateException("Route(id=" + rid + ") is not existed!");
        }

        context.put("route", route);
        // retrieve data
        List<Consumer> consumers = consumerService.findByService(route.getService());
        context.put("consumers", consumers);

        Map<String, Boolean> matchRoute = new HashMap<String, Boolean>();
        for (Consumer c : consumers) {
            matchRoute.put(c.getAddress(), RouteUtils.matchRoute(c.getAddress(), null, route, null));
        }
        context.put("matchRoute", matchRoute);
    }

    public void preview(Map<String, Object> context) throws Exception {
        String rid = (String) context.get("id");
        String consumerid = (String) context.get("cid");


        if (StringUtils.isEmpty(rid)) {
            context.put("message", getMessage("MissRequestParameters", "id"));
        }

        Map<String, String> serviceUrls = new HashMap<String, String>();
        Route route = routeService.findRoute(Long.valueOf(rid));
        if (null == route) {
            context.put("message", getMessage("NoSuchRecord"));
        }
        List<Provider> providers = providerService.findByService(route.getService());
        if (providers != null) {
            for (Provider p : providers) {
                serviceUrls.put(p.getUrl(), p.getParameters());
            }
        }
        if (StringUtils.isNotEmpty(consumerid)) {
            Consumer consumer = consumerService.findConsumer(Long.valueOf(consumerid));
            if (null == consumer) {
                context.put("message", getMessage("NoSuchRecord"));
            }
            Map<String, String> result = RouteUtils.previewRoute(consumer.getService(), consumer.getAddress(), consumer.getParameters(), serviceUrls,
                    route, null, null);
            context.put("route", route);
            context.put("consumer", consumer);
            context.put("result", result);
        } else {
            String address = (String) context.get("address");
            String service = (String) context.get("service");

            Map<String, String> result = RouteUtils.previewRoute(service, address, null, serviceUrls,
                    route, null, null);
            context.put("route", route);

            Consumer consumer = new Consumer();
            consumer.setService(service);
            consumer.setAddress(address);
            context.put("consumer", consumer);
            context.put("result", result);
        }

    }
}
