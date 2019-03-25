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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.OverrideService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.Override;
import com.alibaba.dubboadmin.registry.common.domain.Provider;
import com.alibaba.dubboadmin.registry.common.route.OverrideUtils;
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

/**
 * <p>ProvidersController.</p>
 * URI: <br>
 * GET /providers, show all providers<br>
 * GET /providers/add, show web form for add a static provider<br>
 * POST /provider/create, create a static provider, save form<br>
 * GET /providers/$id, show provider details<br>
 * GET /providers/$id/edit, web form for edit provider<br>
 * POST /providers/$id, update provider, save form<br>
 * GET /providers/$id/delete, delete a provider<br>
 * GET /providers/$id/tostatic, transfer to static<br>
 * GET /providers/$id/todynamic, transfer to dynamic<br>
 * GET /providers/$id/enable, enable a provider<br>
 * GET /providers/$id/disable, disable a provider<br>
 * GET /providers/$id/reconnect, reconnect<br>
 * GET /providers/$id/recover, recover<br>
 * <br>
 * GET /services/$service/providers, show all provider of a specific service<br>
 * GET /services/$service/providers/add, show web form for add a static provider<br>
 * POST /services/$service/providers, save a static provider<br>
 * GET /services/$service/providers/$id, show provider details<br>
 * GET /services/$service/providers/$id/edit, show web form for edit provider<br>
 * POST /services/$service/providers/$id, save changes of provider<br>
 * GET /services/$service/providers/$id/delete, delete provider<br>
 * GET /services/$service/providers/$id/tostatic, transfer to static<br>
 * GET /services/$service/providers/$id/todynamic, transfer to dynamic<br>
 * GET /services/$service/providers/$id/enable, enable<br>
 * GET /services/$service/providers/$id/disable, diable<br>
 * GET /services/$service/providers/$id/reconnect, reconnect<br>
 * GET /services/$service/providers/$id/recover, recover<br>
 *
 */
@Controller
@RequestMapping("/governance/providers")
public class ProvidersController extends BaseController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private OverrideService overrideService;



    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {


        prepare(request, response, model, "index", "providers");

        String value = "";
        String separators = "....";

        List<Provider> providers = null;
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        String address = (String)newModel.get("address");
        String application = (String)newModel.get("app");

        // service
        if (service != null && service.length() > 0) {
            providers = providerService.findByService(service);

            value = service + separators + request.getRequestURI();
        }
        // address
        else if (address != null && address.length() > 0) {
            providers = providerService.findByAddress(address);

            value = address + separators + request.getRequestURI();
        }
        // application
        else if (application != null && application.length() > 0) {
            providers = providerService.findByApplication(application);

            value = application + separators + request.getRequestURI();
        }
        // all
        else {
            providers = providerService.findAll();
        }

        model.addAttribute("providers", providers);
        model.addAttribute("serviceAppMap", getServiceAppMap(providers));

        // record search history to cookies
        try {
            setSearchHistroy(value, request, response);
        } catch (Exception e) {
            //
        }
        return "governance/screen/providers/index";
    }

    /**
     *
     * Calculate the application list corresponding to each service, to facilitate the "repeat" prompt on service page
     * @param providers app services
     */
    private Map<String, Set<String>> getServiceAppMap(List<Provider> providers) {
        Map<String, Set<String>> serviceAppMap = new HashMap<String, Set<String>>();
        if (providers != null && providers.size() > 0) {
            for (Provider provider : providers) {
                Set<String> appSet;
                String service = provider.getService();
                if (serviceAppMap.get(service) == null) {
                    appSet = new HashSet<String>();
                } else {
                    appSet = serviceAppMap.get(service);
                }
                appSet.add(provider.getApplication());
                serviceAppMap.put(service, appSet);
            }
        }
        return serviceAppMap;
    }

    /**
     * Record search history to cookies, steps:
     * Check whether the added record exists in the cookie, and if so, update the list order; if it does not exist, insert it to the front
     *
     * @param value
     */
    private void setSearchHistroy(String value, HttpServletRequest request, HttpServletResponse response) {
        //System.out.println("add new cookie: " + value);
        // Analyze existing cookies
        String separatorsB = "\\.\\.\\.\\.\\.\\.";
        String newCookiev = value;
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("HISTORY")) {
                String cookiev = c.getValue();
                String[] values = cookiev.split(separatorsB);
                int count = 1;
                for (String v : values) {
                    if (count <= 10) {
                        if (!value.equals(v)) {
                            newCookiev = newCookiev + separatorsB + v;
                            //System.out.println("new cookie: " + newCookiev);
                        }
                    }
                    count++;
                }
                break;
            }
        }

        Cookie _cookie = new Cookie("HISTORY", newCookiev);
        _cookie.setMaxAge(60 * 60 * 24 * 7); // Set the cookie's lifetime to 30 minutes
        _cookie.setPath("/");
        response.addCookie(_cookie); // Write to client hard disk
    }

    @RequestMapping("/{id}")
    public String show(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "show", "providers");
        Provider provider = providerService.findProvider(id);
        if (provider != null && provider.isDynamic()) {
            List<Override> overrides = overrideService.findByServiceAndAddress(provider.getService(), provider.getAddress());
            OverrideUtils.setProviderOverrides(provider, overrides);
        }
        model.addAttribute("provider", provider);
        return "governance/screen/providers/show";

    }

    /**
     * Load new service page, get all the service name
     */

    @RequestMapping("/{id}/add")
    public String add(@PathVariable("id") Long id,
                      HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("id", id);
        return add(request, response, model);
    }

    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "add", "providers");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        Long id = (Long)newModel.get("id");
        String service = (String)newModel.get("service");
        if (service == null) {
            List<String> serviceList = Tool.sortSimpleName(new ArrayList<String>(providerService.findServices()));
            model.addAttribute("serviceList", serviceList);
        }
        if (id != null) {
            Provider p = providerService.findProvider(id);
            if (p != null) {
                model.addAttribute("provider", p);
                String parameters = p.getParameters();
                if (parameters != null && parameters.length() > 0) {
                    Map<String, String> map = StringUtils.parseQueryString(parameters);
                    map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    map.remove("pid");
                    p.setParameters(StringUtils.toQueryString(map));
                }
            }
        }
        return "governance/screen/providers/add";
    }

    @RequestMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id,  HttpServletRequest request, HttpServletResponse response,  Model model) {
        prepare(request, response, model,"edit", "providers");
        Provider provider = providerService.findProvider(id);
        if (provider != null && provider.isDynamic()) {
            List<Override> overrides = overrideService.findByServiceAndAddress(provider.getService(), provider.getAddress());
            OverrideUtils.setProviderOverrides(provider, overrides);
        }
        model.addAttribute("provider", provider);
        return "governance/screen/providers/edit";
    }

    @RequestMapping(value =  "/create", method = RequestMethod.POST)  //post
    public String create(@ModelAttribute Provider provider, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model,"create" ,"providers");
        boolean success = true;
        String service = provider.getService();
        if (service == null) {
            service = (String)((BindingAwareModelMap)model).get("service");
            provider.setService(service);
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../providers");
            return "governance/screen/redirect";
        }
        if (provider.getParameters() == null) {
            String url = provider.getUrl();
            if (url != null) {
                int i = url.indexOf('?');
                if (i > 0) {
                    provider.setUrl(url.substring(0, i));
                    provider.setParameters(url.substring(i + 1));
                }
            }
        }
        provider.setDynamic(false); // Provider add through web page must be static
        providerService.create(provider);
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST) //post
    public String update(@ModelAttribute Provider newProvider, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "providers");
        boolean success = true;
        Long id = newProvider.getId();
        String parameters = newProvider.getParameters();
        Provider provider = providerService.findProvider(id);
        if (provider == null) {
            model.addAttribute("message", getMessage("NoSuchOperationData", id));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../providers");
            return "governance/screen/redirect";
        }
        String service = provider.getService();
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            success = false;
            model.addAttribute("success", success);
            model.addAttribute("redirect", "../providers");
            return "governance/screen/redirect";
        }
        Map<String, String> oldMap = StringUtils.parseQueryString(provider.getParameters());
        Map<String, String> newMap = StringUtils.parseQueryString(parameters);
        for (Map.Entry<String, String> entry : oldMap.entrySet()) {
            if (entry.getValue().equals(newMap.get(entry.getKey()))) {
                newMap.remove(entry.getKey());
            }
        }
        if (provider.isDynamic()) {
            String address = provider.getAddress();
            List<Override> overrides = overrideService.findByServiceAndAddress(provider.getService(), provider.getAddress());
            OverrideUtils.setProviderOverrides(provider, overrides);
            Override override = provider.getOverride();
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
        } else {
            provider.setParameters(parameters);
            providerService.updateProvider(provider);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/delete")
    public String delete(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "delete", "providers");
        boolean success = true;
        for (Long id : ids) {
            Provider provider = providerService.findProvider(id);
            if (provider == null) {
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (provider.isDynamic()) {
                model.addAttribute("message", getMessage("CanNotDeleteDynamicData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (!super.currentUser.hasServicePrivilege(provider.getService())) {
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", provider.getService()));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            }
        }
        for (Long id : ids) {
            providerService.deleteStaticProvider(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/enable")
    public String enable(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "enable", "providers");
        boolean success = true;
        Map<Long, Provider> id2Provider = new HashMap<Long, Provider>();
        for (Long id : ids) {
            Provider provider = providerService.findProvider(id);
            if (provider == null) {
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (!super.currentUser.hasServicePrivilege(provider.getService())) {
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", provider.getService()));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            }
            id2Provider.put(id, provider);
        }
        for (Long id : ids) {
            providerService.enableProvider(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/disable")
    public String disable(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response,  Model model) {
        prepare(request, response, model, "disable", "providers");
        boolean success = true;
        for (Long id : ids) {
            Provider provider = providerService.findProvider(id);
            if (provider == null) {
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (!super.currentUser.hasServicePrivilege(provider.getService())) {
                success = false;
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", provider.getService()));
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            }
        }
        for (Long id : ids) {
            providerService.disableProvider(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/doubling")
    public String doubling(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response,  Model model) {
        prepare(request, response, model, "doubling","providers");
        boolean success = true;
        for (Long id : ids) {
            Provider provider = providerService.findProvider(id);
            if (provider == null) {
                success = false;
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (!super.currentUser.hasServicePrivilege(provider.getService())) {
                success = false;
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", provider.getService()));
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            }
        }
        for (Long id : ids) {
            providerService.doublingProvider(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../providers");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/halving")
    public String halving(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "halving","providers");
        boolean success = true;
        for (Long id : ids) {
            Provider provider = providerService.findProvider(id);
            if (provider == null) {
                success = false;
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            } else if (!super.currentUser.hasServicePrivilege(provider.getService())) {
                success = false;
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", provider.getService()));
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../providers");
                return "governance/screen/redirect";
            }
        }
        for (Long id : ids) {
            providerService.halvingProvider(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../providers");
        return "governance/screen/redirect";
    }

}
