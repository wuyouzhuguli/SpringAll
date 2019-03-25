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

package com.alibaba.dubboadmin.web.mvc;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubboadmin.governance.biz.common.i18n.MessageResourceService;
import com.alibaba.dubboadmin.governance.util.WebConstants;
import com.alibaba.dubboadmin.registry.common.domain.User;
import com.alibaba.dubboadmin.web.pulltool.RootContextPath;
import com.alibaba.dubboadmin.web.pulltool.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected static final Pattern SPACE_SPLIT_PATTERN = Pattern.compile("\\s+");
    //FIXME, to extract these auxiliary methods
    protected String role = null;
    protected String operator = null;
    protected User currentUser = null;
    protected String operatorAddress = null;
    protected String currentRegistry = null;
    @Autowired
    private MessageResourceService messageResourceService;

    @Autowired
    protected Tool tool;

    public void prepare(HttpServletRequest request, HttpServletResponse response, Model model,
                        String methodName, String type) {
        if (request.getSession().getAttribute(WebConstants.CURRENT_USER_KEY) != null) {
            User user = (User) request.getSession().getAttribute(WebConstants.CURRENT_USER_KEY);
            currentUser = user;
            operator = user.getUsername();
            role = user.getRole();
            request.getSession().setAttribute(WebConstants.CURRENT_USER_KEY, user);
        }
        operatorAddress = request.getRemoteHost();
        request.getMethod();
        model.addAttribute("operator", operator);
        model.addAttribute("operatorAddress", operatorAddress);

        model.addAttribute("currentRegistry", currentRegistry);
        model.addAttribute("rootContextPath", new RootContextPath(request.getContextPath()));
        model.addAttribute("tool", tool);
        model.addAttribute("_method", methodName);
        model.addAttribute("helpUrl", WebConstants.HELP_URL);
        model.addAttribute("_type", type);

    }

    public String getMessage(String key, Object... args) {
        return messageResourceService.getMessage(key, args);
    }

    private String getDefaultRedirect(Map<String, Object> context, String operate) {
        String defaultRedirect = (String) context.get("defaultRedirect");
        return defaultRedirect;
    }

}
