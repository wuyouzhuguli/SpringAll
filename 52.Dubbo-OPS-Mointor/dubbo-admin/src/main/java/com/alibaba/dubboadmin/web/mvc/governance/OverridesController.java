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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.ConsumerService;
import com.alibaba.dubboadmin.governance.service.OverrideService;
import com.alibaba.dubboadmin.governance.service.ProviderService;
import com.alibaba.dubboadmin.registry.common.domain.Override;
import com.alibaba.dubboadmin.web.mvc.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/governance/overrides")
public class OverridesController extends BaseController {
    static final Pattern AND = Pattern.compile("\\&");
    static final Pattern EQUAL = Pattern.compile("([^=\\s]*)\\s*=\\s*(\\S*)");
    static final String DEFAULT_MOCK_JSON_KEY = "mock";
    static final String MOCK_JSON_KEY_POSTFIX = ".mock";
    static final String FORM_OVERRIDE_KEY = "overrideKey";
    static final String FORM_OVERRIDE_VALUE = "overrideValue";
    static final String FORM_DEFAULT_MOCK_METHOD_FORCE = "mockDefaultMethodForce";
    static final String FORM_DEFAULT_MOCK_METHOD_JSON = "mockDefaultMethodJson";
    static final String FORM_ORIGINAL_METHOD_FORCE_PREFIX = "mockMethodForce.";
    static final String FORM_ORIGINAL_METHOD_PREFIX = "mockMethod.";
    static final String FORM_DYNAMIC_METHOD_NAME_PREFIX = "mockMethodName";
    static final String FORM_DYNAMIC_METHOD_FORCE_PREFIX = "mockMethodForce";
    static final String FORM_DYNAMIC_METHOD_JSON_PREFIX = "mockMethodJson";
    @Autowired
    private OverrideService overrideService;

    // FORM KEY
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ConsumerService consumerService;

    static Map<String, String> parseQueryString(String query) {
        HashMap<String, String> ret = new HashMap<String, String>();
        if (query == null || (query = query.trim()).length() == 0) return ret;

        String[] kvs = AND.split(query);
        for (String kv : kvs) {
            Matcher matcher = EQUAL.matcher(kv);
            if (!matcher.matches()) continue;
            String key = matcher.group(1);
            String value = matcher.group(2);
            ret.put(key, value);
        }

        return ret;
    }

    @RequestMapping("")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model, "index", "overrides");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String service = (String)newModel.get("service");
        String application = (String)newModel.get("app");
        String address = (String)newModel.get("address");
        List<Override> overrides;
        if (StringUtils.isNotEmpty(service)) {
            overrides = overrideService.findByService(service);
        } else if (StringUtils.isNotEmpty(application)) {
            overrides = overrideService.findByApplication(application);
        } else if (StringUtils.isNotEmpty(address)) {
            overrides = overrideService.findByAddress(address);
        } else {
            overrides = overrideService.findAll();
        }
        model.addAttribute("overrides", overrides);
        return "governance/screen/overrides/index";
    }

    @RequestMapping("/{id}")
    public String show(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response,
                     Model model) {
        prepare(request, response, model, "show", "overrides");
        Override override = overrideService.findById(id);

        Map<String, String> parameters = parseQueryString(override.getParams());

        if (parameters.get(DEFAULT_MOCK_JSON_KEY) != null) {
            String mock = URL.decode(parameters.get(DEFAULT_MOCK_JSON_KEY));
            String[] tokens = parseMock(mock);
            model.addAttribute(FORM_DEFAULT_MOCK_METHOD_FORCE, tokens[0]);
            model.addAttribute(FORM_DEFAULT_MOCK_METHOD_JSON, tokens[1]);
            parameters.remove(DEFAULT_MOCK_JSON_KEY);
        }

        Map<String, String> method2Force = new LinkedHashMap<String, String>();
        Map<String, String> method2Json = new LinkedHashMap<String, String>();

        for (Iterator<Entry<String, String>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> e = iterator.next();
            String key = e.getKey();

            if (key.endsWith(MOCK_JSON_KEY_POSTFIX)) {
                String m = key.substring(0, key.length() - MOCK_JSON_KEY_POSTFIX.length());
                parseMock(m, e.getValue(), method2Force, method2Json);
                iterator.remove();
            }
        }

        model.addAttribute("methodForces", method2Force);
        model.addAttribute("methodJsons", method2Json);
        model.addAttribute("parameters", parameters);
        model.addAttribute("override", override);
        return "governance/screen/overrides/show";
    }

    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        prepare(request, response, model,"add", "overrides");
        BindingAwareModelMap newModel = (BindingAwareModelMap)model;
        String application = (String)newModel.get("app");
        String service = (String)newModel.get("service");
        List<String> serviceList = new ArrayList<String>();
        List<String> applicationList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(application)) {
            serviceList.addAll(providerService.findServicesByApplication(application));
            serviceList.addAll(consumerService.findServicesByApplication(application));
            model.addAttribute("serviceList", serviceList);
        } else if (StringUtils.isNotEmpty(service)) {
            applicationList.addAll(providerService.findApplicationsByServiceName(service));
            applicationList.addAll(consumerService.findApplicationsByServiceName(service));
            model.addAttribute("applicationList", applicationList);
        } else {
            serviceList.addAll(providerService.findServices());
            serviceList.addAll(consumerService.findServices());
            providerService.findServicesByApplication(application);
            consumerService.findServicesByApplication(application);
        }
        model.addAttribute("serviceList", serviceList);

        if (StringUtils.isNotEmpty(service) && !service.contains("*")) {
            model.addAttribute("methods", CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(service))));
        }
        return "governance/screen/overrides/add";
    }

    @RequestMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response,
                     Model model) {
        prepare(request, response, model, "edit", "overrides");
        Override override = overrideService.findById(id);

        Map<String, String> parameters = parseQueryString(override.getParams());

        if (parameters.get(DEFAULT_MOCK_JSON_KEY) != null) {
            String mock = URL.decode(parameters.get(DEFAULT_MOCK_JSON_KEY));
            String[] tokens = parseMock(mock);
            model.addAttribute(FORM_DEFAULT_MOCK_METHOD_FORCE, tokens[0]);
            model.addAttribute(FORM_DEFAULT_MOCK_METHOD_JSON, tokens[1]);
            parameters.remove(DEFAULT_MOCK_JSON_KEY);
        }

        Map<String, String> method2Force = new LinkedHashMap<String, String>();
        Map<String, String> method2Json = new LinkedHashMap<String, String>();

        List<String> methods = CollectionUtils.sort(new ArrayList<String>(providerService.findMethodsByService(override.getService())));
        if (methods != null && methods.isEmpty()) {
            for (String m : methods) {
                parseMock(m, parameters.get(m + MOCK_JSON_KEY_POSTFIX), method2Force, method2Json);
                parameters.remove(m + MOCK_JSON_KEY_POSTFIX);
            }
        }
        for (Iterator<Entry<String, String>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> e = iterator.next();
            String key = e.getKey();

            if (key.endsWith(MOCK_JSON_KEY_POSTFIX)) {
                String m = key.substring(0, key.length() - MOCK_JSON_KEY_POSTFIX.length());
                parseMock(m, e.getValue(), method2Force, method2Json);
                iterator.remove();
            }
        }

        model.addAttribute("methods", methods);
        model.addAttribute("methodForces", method2Force);
        model.addAttribute("methodJsons", method2Json);
        model.addAttribute("parameters", parameters);
        model.addAttribute("override", override);
        return "governance/screen/overrides/edit";
    }

    private void parseMock(String m, String mock, Map<String, String> method2Force, Map<String, String> method2Json) {
        String[] tokens = parseMock(mock);
        method2Force.put(m, tokens[0]);
        method2Json.put(m, tokens[1]);
    }

    private String[] parseMock(String mock) {
        mock = URL.decode(mock);
        String force;
        if (mock.startsWith("force:")) {
            force = "force";
            mock = mock.substring("force:".length());
        } else if (mock.startsWith("fail:")) {
            force = "fail";
            mock = mock.substring("fail:".length());
        } else {
            force = "fail";
        }
        String[] tokens = new String[2];
        tokens[0] = force;
        tokens[1] = mock;
        return tokens;
    }

    boolean catchParams(Override override, HttpServletRequest request, Model model) {
        Map<String, String[]> map = request.getParameterMap();
        String service = map.get("service")[0];
        if (service == null || service.trim().length() == 0) {
            model.addAttribute("message", getMessage("service is blank!"));
            return false;
        }
        if (!super.currentUser.hasServicePrivilege(service)) {
            model.addAttribute("message", getMessage("HaveNoServicePrivilege", service));
            return false;
        }

        String defaultMockMethodForce = map.get(FORM_DEFAULT_MOCK_METHOD_FORCE)[0];
        String defaultMockMethodJson = map.get(FORM_DEFAULT_MOCK_METHOD_JSON)[0];

        Map<String, String> override2Value = new HashMap<String, String>();
        Map<String, String> method2Json = new HashMap<String, String>();

        for (Map.Entry<String, String[]> param : map.entrySet()) {
            String key = param.getKey().trim();
            if(param.getValue().length != 1) continue;;

            String value = param.getValue()[0];

            if (key.startsWith(FORM_OVERRIDE_KEY) && value != null && value.trim().length() > 0) {
                String index = key.substring(FORM_OVERRIDE_KEY.length());
                String overrideValue = map.get(FORM_OVERRIDE_VALUE + index)[0];
                if (overrideValue != null && overrideValue.trim().length() > 0) {
                    override2Value.put(value.trim(), overrideValue.trim());
                }
            }

            if (key.startsWith(FORM_ORIGINAL_METHOD_PREFIX) && value != null && value.trim().length() > 0) {
                String method = key.substring(FORM_ORIGINAL_METHOD_PREFIX.length());
                String force = map.get(FORM_ORIGINAL_METHOD_FORCE_PREFIX + method)[0];
                method2Json.put(method, force + ":" + value.trim());
            }

            if (key.startsWith(FORM_DYNAMIC_METHOD_NAME_PREFIX) && value != null && value.trim().length() > 0) {
                String index = key.substring(FORM_DYNAMIC_METHOD_NAME_PREFIX.length());
                String force = map.get(FORM_DYNAMIC_METHOD_FORCE_PREFIX + index)[0];
                String json =  map.get(FORM_DYNAMIC_METHOD_JSON_PREFIX + index)[0];

                if (json != null && json.trim().length() > 0) {
                    method2Json.put(value.trim(), force + ":" + json.trim());
                }
            }
        }

        StringBuilder paramters = new StringBuilder();
        boolean isFirst = true;
        if (defaultMockMethodJson != null && defaultMockMethodJson.trim().length() > 0) {
            paramters.append("mock=").append(URL.encode(defaultMockMethodForce + ":" + defaultMockMethodJson.trim()));
            isFirst = false;
        }
        for (Map.Entry<String, String> e : method2Json.entrySet()) {
            if (isFirst) isFirst = false;
            else paramters.append("&");

            paramters.append(e.getKey()).append(MOCK_JSON_KEY_POSTFIX).append("=").append(URL.encode(e.getValue()));
        }
        for (Map.Entry<String, String> e : override2Value.entrySet()) {
            if (isFirst) isFirst = false;
            else paramters.append("&");

            paramters.append(e.getKey()).append("=").append(URL.encode(e.getValue()));
        }

        String p = paramters.toString();
        if (p.trim().length() == 0) {
            model.addAttribute("message", getMessage("Please enter Parameters!"));
            return false;
        }

        override.setParams(p);
        return true;
    }


    @RequestMapping("/create")
    public String create(Override override, HttpServletRequest request,
                         HttpServletResponse response, Model model) {
        prepare(request,response,model,"update", "overrides");
        boolean success = true;
        if (!catchParams(override, request, model)) {
            success =false;
        } else {
            overrideService.saveOverride(override);
        }

        model.addAttribute("success", success);
        model.addAttribute("redirect", "../overrides");
        return "governance/screen/redirect";
    }

    @RequestMapping("/update")
    public String update(Override override, HttpServletRequest request,
                         HttpServletResponse response, Model model) {
        prepare(request, response, model, "update", "overrides");
        boolean  succcess = true;
        Override o = overrideService.findById(override.getId());
        override.setService(o.getService());
        override.setAddress(o.getAddress());
        override.setApplication(o.getApplication());

        if (!catchParams(override, request, model)) {
            succcess = false;
        } else {
            overrideService.updateOverride(override);
        }

        model.addAttribute("success", succcess);
        model.addAttribute("redirect", "../overrides");
        return "governance/screen/redirect";

    }

    @RequestMapping("/{ids}/delete")
    public String delete(@PathVariable("ids") Long[] ids, HttpServletRequest request,
                              HttpServletResponse response, Model model) {
        prepare(request, response, model, "delete", "overrides");
        for (Long id : ids) {
            overrideService.deleteOverride(id);
        }

        model.addAttribute("success", true);
        model.addAttribute("redirect", "../../overrides");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/enable")
    public String enable(@PathVariable("ids") Long[] ids, HttpServletRequest request,
                          HttpServletResponse response, Model model) {
        prepare(request, response, model, "enable", "overrides");
        boolean success = true;
        for (Long id : ids) {
            Override override = overrideService.findById(id);
            if (override == null) {
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../overrides");
                return "governance/screen/redirect";
            } else {
                if (!super.currentUser.hasServicePrivilege(override.getService())) {
                    model.addAttribute("message", getMessage("HaveNoServicePrivilege", override.getService()));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../overrides");
                    return "governance/screen/redirect";
                }
            }
        }

        for (Long id : ids) {
            overrideService.enableOverride(id);
        }

        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../overrides");
        return "governance/screen/redirect";
    }

    @RequestMapping("/{ids}/disable")
    public String disable(@PathVariable("ids") Long[] ids, HttpServletRequest request,
                           HttpServletResponse response, Model model) {
        prepare(request, response, model, "disable", "overrides");
        boolean success = true;
        for (Long id : ids) {
            Override override = overrideService.findById(id);
            if (override == null) {
                model.addAttribute("message", getMessage("NoSuchOperationData", id));
                success = false;
                model.addAttribute("success", success);
                model.addAttribute("redirect", "../../overrides");
                return "governance/screen/redirect";
            } else {
                if (!super.currentUser.hasServicePrivilege(override.getService())) {
                    model.addAttribute("message", getMessage("HaveNoServicePrivilege", override.getService()));
                    success = false;
                    model.addAttribute("success", success);
                    model.addAttribute("redirect", "../../overrides");
                    return "governance/screen/redirect";
                }
            }
        }

        for (Long id : ids) {
            overrideService.disableOverride(id);
        }
        model.addAttribute("success", success);
        model.addAttribute("redirect", "../../overrides");
        return "governance/screen/redirect";
    }

}
