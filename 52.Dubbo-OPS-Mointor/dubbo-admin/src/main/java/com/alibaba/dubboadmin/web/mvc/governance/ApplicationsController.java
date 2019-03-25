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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ProvidersController.
 * URI: /applications
 *
 */
@Controller
@RequestMapping("/governance/applications")
public class ApplicationsController extends BaseController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private OverrideService overrideService;

    @Autowired
    ServicesController servicesController;

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "applications");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        String application = (String)newModel.get("app");
        String address = (String)newModel.get("address");
        String keyword = request.getParameter("keyword");
        if (service != null) {
            Set<String> applications = new TreeSet<String>();
            List<String> providerApplications = providerService.findApplicationsByServiceName(service);
            if (providerApplications != null && providerApplications.size() > 0) {
                applications.addAll(providerApplications);
            }
            List<String> consumerApplications = consumerService.findApplicationsByServiceName(service);
            if (consumerApplications != null && consumerApplications.size() > 0) {
                applications.addAll(consumerApplications);
            }
            model.addAttribute("applications", applications);
            model.addAttribute("providerApplications", providerApplications);
            model.addAttribute("consumerApplications", consumerApplications);
            if (service != null && service.length() > 0) {
                List<Override> overrides = overrideService.findByService(service);
                Map<String, List<Override>> application2Overrides = new HashMap<String, List<Override>>();
                if (overrides != null && overrides.size() > 0
                        && applications != null && applications.size() > 0) {
                    for (String a : applications) {
                        if (overrides != null && overrides.size() > 0) {
                            List<Override> appOverrides = new ArrayList<Override>();
                            for (Override override : overrides) {
                                if (override.isMatch(service, null, a)) {
                                    appOverrides.add(override);
                                }
                            }
                            Collections.sort(appOverrides, OverrideUtils.OVERRIDE_COMPARATOR);
                            application2Overrides.put(a, appOverrides);
                        }
                    }
                }
                model.addAttribute("overrides", application2Overrides);
            }
            return "governance/screen/applications/index";
        }
        if (service == null && application == null
                && address == null) {
            model.addAttribute("app", "*");
        }
        Set<String> applications = new TreeSet<String>();
        List<String> providerApplications = providerService.findApplications();
        if (providerApplications != null && providerApplications.size() > 0) {
            applications.addAll(providerApplications);
        }
        List<String> consumerApplications = consumerService.findApplications();
        if (consumerApplications != null && consumerApplications.size() > 0) {
            applications.addAll(consumerApplications);
        }

        Set<String> newList = new HashSet<String>();
        Set<String> newProviders = new HashSet<String>();
        Set<String> newConsumers = new HashSet<String>();
        model.addAttribute("applications", applications);
        model.addAttribute("providerApplications", providerApplications);
        model.addAttribute("consumerApplications", consumerApplications);

        if (StringUtils.isNotEmpty(keyword) && !"*".equals(keyword)) {
            keyword = keyword.toLowerCase();
            for (String o : applications) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newList.add(o);
                }
            }
            for (String o : providerApplications) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newProviders.add(o);
                }
            }
            for (String o : consumerApplications) {
                if (o.toLowerCase().indexOf(keyword) != -1) {
                    newConsumers.add(o);
                }
            }
            model.addAttribute("applications", newList);
            model.addAttribute("providerApplications", newProviders);
            model.addAttribute("consumerApplications", newConsumers);
        }
        return "governance/screen/applications/index";
    }

    //@RequestMapping("/{application}/services")
    //public String getService(@PathVariable("application") String app, HttpServletRequest request,
    //                         HttpServletResponse response,
    //                         Model model) {
    //    return servicesController.index(app, null, null, null, request, response, model);
    //}

    //@RequestMapping("/{application}/services/{serviceName}/{type}")
    //public String serviceMapping(@PathVariable("application") String app,
    //                             @PathVariable("serviceName") String serviceName,
    //                             @PathVariable("type") String type, HttpServletRequest request, HttpServletResponse response,
    //                             Model model) {
    //    return servicesController.route(serviceName, type, app, null, request, response, model);
    //}

    //public void search(@RequestParam(required = false) String service,
    //                   @RequestParam(required = false) String address,
    //                   @RequestParam(required = false) String application,
    //                   @RequestParam(required = false) String keyword,
    //                   HttpServletRequest request, HttpServletResponse response, Model model) {
    //    index(service, address, application, keyword, request, response, model);
    //
    //    //Set<String> newList = new HashSet<String>();
    //    //@SuppressWarnings("unchecked")
    //    //Set<String> apps = (Set<String>) .get("applications");
    //    //String keyword = (String) context.get("keyword");
    //    //if (StringUtils.isNotEmpty(keyword)) {
    //    //    keyword = keyword.toLowerCase();
    //    //    for (String o : apps) {
    //    //        if (o.toLowerCase().indexOf(keyword) != -1) {
    //    //            newList.add(o);
    //    //        }
    //    //    }
    //    //}
    //    //context.put("applications", newList);
    //}

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
        prepare(request, response, model, methodName, "applications");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String) newModel.get("service");
        String applications = (String) newModel.get("app");
        if (service == null || service.length() == 0
                || applications == null || applications.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../applications");
            return "governance/screen/redirect";
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../applications");
            return "governance/screen/redirect";
        }
        for (String application : SPACE_SPLIT_PATTERN.split(applications)) {
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
        model.addAttribute("redirect", "../../applications");
        return "governance/screen/redirect";
    }

    @RequestMapping("/allshield")
    public String allshield(@RequestParam(required = false) String service, HttpServletRequest request,
                             HttpServletResponse response, Model model) throws Exception {
        return allmock(service, "force:return null", "allshield", request, response, model);
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
        prepare(request, response, model, methodName, "applications");
        if (service == null || service.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../applications");
            return "governance/screen/redirect";
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../applications");
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
        model.addAttribute("success", false);
        model.addAttribute("redirect", "../../applications");
        return "governance/screen/redirect";
    }

}
