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

import com.alibaba.dubboadmin.governance.service.OwnerService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.Owner;
import com.alibaba.dubboadmin.web.mvc.BaseController;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ProvidersController. URI: /services/$service/owners
 *
 */
@Controller
@RequestMapping("/governance/owners")
public class OwnersController extends BaseController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private ProviderService providerService;

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "owners");
        List<Owner> owners;
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        if (service != null && service.length() > 0) {
            owners = ownerService.findByService(service);
        } else {
            owners = ownerService.findAll();
        }
        model.addAttribute("owners", owners);
        return "governance/screen/owners/index";
    }

    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "add", "owners");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        if (service == null || service.length() == 0) {
            List<String> serviceList = Tool.sortSimpleName(new ArrayList<String>(providerService.findServices()));
            model.addAttribute("serviceList", serviceList);
        }
        return "governance/screen/owners/add";
    }

    @RequestMapping(value =  "/create", method = RequestMethod.POST)  //post
    public String create(Owner owner, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "create", "owners");
        String service = owner.getService();
        String username = owner.getUsername();
        if (service == null || service.length() == 0
                || username == null || username.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../owners");
            return "governance/screen/redirect";
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../owners");
            return "governance/screen/redirect";
        }
        ownerService.saveOwner(owner);
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../owners");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/delete")
    public String delete(@PathVariable("ids") Long[] ids, HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "delete", "owners");

        String service = request.getParameter("service");
        String username = request.getParameter("username");
        Owner owner = new Owner();
        owner.setService(service);
        owner.setUsername(username);
        if (service == null || service.length() == 0
                || username == null || username.length() == 0) {
            model.addAttribute("message", getMessage("NoSuchOperationData"));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../owners");
            return "governance/screen/redirect";
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            model.addAttribute("success", false);
            model.addAttribute("redirect", "../../owners");
            return "governance/screen/redirect";
        }
        ownerService.deleteOwner(owner);
        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../owners");
        return "governance/screen/redirect";
    }

}
