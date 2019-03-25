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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubboadmin.governance.service.OverrideService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.LoadBalance;
import com.alibaba.dubboadmin.registry.common.domain.Provider;
import com.alibaba.dubboadmin.registry.common.util.OverrideUtils;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ProvidersController.
 * URI: /services/$service/loadbalances
 *
 */
@Controller
@RequestMapping("/governance/loadbalances")
public class LoadbalancesController extends BaseController {

    @Autowired
    private OverrideService overrideService;

    @Autowired
    private ProviderService providerService;

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "loadbalances");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        service = StringUtils.trimToNull(service);

        List<LoadBalance> loadbalances;
        if (service != null && service.length() > 0) {
            loadbalances = OverrideUtils.overridesToLoadBalances(overrideService.findByService(service));
        } else {
            loadbalances = OverrideUtils.overridesToLoadBalances(overrideService.findAll());
        }
        model.addAttribute("loadbalances", loadbalances);
        return "governance/screen/loadbalances/index";
    }

    @RequestMapping("/{id}")
    public String show(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "show", "loadbalances");
        LoadBalance loadbalance = OverrideUtils.overrideToLoadBalance(overrideService.findById(id));
        model.addAttribute("loadbalance", loadbalance);
        return "governance/screen/loadbalances/show";
    }

    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "add", "loadbalances");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");

        if (service != null && service.length() > 0 && !service.contains("*")) {
            List<Provider> providerList = providerService.findByService(service);
            List<String> addressList = new ArrayList<String>();
            for (Provider provider : providerList) {
                addressList.add(provider.getUrl().split("://")[1].split("/")[0]);
            }
            model.addAttribute("addressList", addressList);
            model.addAttribute("service", service);
            model.addAttribute("methods", CollectionUtils.sort(providerService.findMethodsByService(service)));
        } else {
            List<String> serviceList = Tool.sortSimpleName(providerService.findServices());
            model.addAttribute("serviceList", serviceList);
        }
        //if (input != null) model.addAttribute("input", input);
        return "governance/screen/loadbalances/add";
    }

    @RequestMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "edit", "loadbalances");
        String service = request.getParameter("service");
        String input = request.getParameter("input");

        if (service != null && service.length() > 0 && !service.contains("*")) {
            List<Provider> providerList = providerService.findByService(service);
            List<String> addressList = new ArrayList<String>();
            for (Provider provider : providerList) {
                addressList.add(provider.getUrl().split("://")[1].split("/")[0]);
            }
            model.addAttribute("addressList", addressList);
            model.addAttribute("service", service);
            model.addAttribute("methods", CollectionUtils.sort(providerService.findMethodsByService(service)));
        } else {
            List<String> serviceList = Tool.sortSimpleName(providerService.findServices());
            model.addAttribute("serviceList", serviceList);
        }
        if (input != null) model.addAttribute("input", input);
        LoadBalance loadbalance = OverrideUtils.overrideToLoadBalance(overrideService.findById(id));
        model.addAttribute("loadbalance", loadbalance);
        return "governance/screen/loadbalances/edit";
    }

    @RequestMapping("/create")
    public String create(LoadBalance loadBalance, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "create", "loadbalances");
        boolean success = true;
        if (!super.currentUser.hasServicePrivilege(loadBalance.getService())) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", loadBalance.getService()));
            success = false;
        } else {
            loadBalance.setUsername((String) ((BindingAwareModelMap)model).get("operator"));
            overrideService.saveOverride(OverrideUtils.loadBalanceToOverride(loadBalance));
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../loadbalances");
        return "governance/screen/redirect";
    }


    @RequestMapping("/update")
    public String update(LoadBalance loadBalance, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "loadbalances");
        boolean success = true;
        if (!super.currentUser.hasServicePrivilege(loadBalance.getService())) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", loadBalance.getService()));
            success = false;
        } else {
            overrideService.updateOverride(OverrideUtils.loadBalanceToOverride(loadBalance));
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../loadbalances");
        return "governance/screen/redirect";

    }

    /**
     *
     * @param ids
     * @return
     */
    @RequestMapping("/{ids}/delete")
    public String delete(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "delete", "loadbalances");
        boolean success = true;
        for (Long id : ids) {
            LoadBalance lb = OverrideUtils.overrideToLoadBalance(overrideService.findById(id));
            if (!super.currentUser.hasServicePrivilege(lb.getService())) {
                model.addAttribute("message", getMessage("HaveNoServicePrivilege", lb.getService()));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../loadbalances");
                return "governance/screen/redirect";
            }
        }

        for (Long id : ids) {
            overrideService.deleteOverride(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../loadbalances");
        return "governance/screen/redirect";
    }

}
