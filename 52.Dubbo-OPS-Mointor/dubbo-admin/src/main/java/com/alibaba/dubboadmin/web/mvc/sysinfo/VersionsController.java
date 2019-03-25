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
package com.alibaba.dubboadmin.web.mvc.sysinfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.Consumer;
import com.alibaba.dubboadmin.registry.common.domain.Provider;
import com.alibaba.dubboadmin.web.mvc.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sysinfo")
public class VersionsController extends BaseController {
    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @RequestMapping("/versions")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "versions");
        List<Provider> providers = providerService.findAll();
        List<Consumer> consumers = consumerService.findAll();
        Set<String> parametersSet = new HashSet<String>();
        for (Provider provider : providers) {
            parametersSet.add(provider.getParameters());
        }
        for (Consumer consumer : consumers) {
            parametersSet.add(consumer.getParameters());
        }
        Map<String, Set<String>> versions = new HashMap<String, Set<String>>();
        Iterator<String> temp = parametersSet.iterator();
        while (temp.hasNext()) {
            Map<String, String> parameter = StringUtils.parseQueryString(temp.next());
            if (parameter != null) {
                String dubbo = parameter.get("dubbo");
                if (dubbo == null) dubbo = "0.0.0";
                String application = parameter.get("application");
                if (versions.get(dubbo) == null) {
                    Set<String> apps = new HashSet<String>();
                    versions.put(dubbo, apps);
                }
                versions.get(dubbo).add(application);
            }
        }
        model.addAttribute("versions", versions);
        return "sysinfo/screen/versions/index";
    }

    @RequestMapping("/version/{version}/versions/show")
    public String show(@PathVariable("version") String version, HttpServletRequest request, HttpServletResponse response,
                     Model model) {
        prepare(request, response, model, "show", "versions");
        if (version != null && version.length() > 0) {
            List<Provider> providers = providerService.findAll();
            List<Consumer> consumers = consumerService.findAll();
            Set<String> parametersSet = new HashSet<String>();
            Set<String> applications = new HashSet<String>();
            for (Provider provider : providers) {
                parametersSet.add(provider.getParameters());
            }
            for (Consumer consumer : consumers) {
                parametersSet.add(consumer.getParameters());
            }
            Iterator<String> temp = parametersSet.iterator();
            while (temp.hasNext()) {
                Map<String, String> parameter = StringUtils.parseQueryString(temp.next());
                if (parameter != null) {
                    String dubbo = parameter.get("dubbo");
                    if (dubbo == null) dubbo = "0.0.0";
                    String application = parameter.get("application");
                    if (version.equals(dubbo)) {
                        applications.add(application);
                    }
                }
            }
            model.addAttribute("applications", applications);
        }
        return "sysinfo/screen/versions/show";
    }

}
