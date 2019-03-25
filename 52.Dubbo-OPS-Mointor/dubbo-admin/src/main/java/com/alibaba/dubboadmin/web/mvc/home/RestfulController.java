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
package com.alibaba.dubboadmin.web.mvc.home;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubboadmin.governance.util.WebConstants;
import com.alibaba.dubboadmin.registry.common.domain.User;
import com.alibaba.fastjson.JSON;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class RestfulController {

    protected String role = null;
    protected String operator = null;

    //    @Autowired
//    RegistryValidator          registryService;
    protected User currentUser = null;
    protected String operatorAddress = null;
    protected URL url = null;
    @Autowired
    HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public void execute(Map<String, Object> context) throws Exception {
        ResultController resultController = new ResultController();
        if (request.getParameter("url") != null) {
            url = URL.valueOf(URL.decode(request.getParameter("url")));
        }
        if (context.get(WebConstants.CURRENT_USER_KEY) != null) {
            User user = (User) context.get(WebConstants.CURRENT_USER_KEY);
            currentUser = user;
            operator = user.getUsername();
            role = user.getRole();
            context.put(WebConstants.CURRENT_USER_KEY, user);
        }
        operatorAddress = (String) context.get("clientid");
        if (operatorAddress == null || operatorAddress.isEmpty()) {
            operatorAddress = (String) context.get("request.remoteHost");
        }
        context.put("operator", operator);
        context.put("operatorAddress", operatorAddress);
        String jsonResult = null;
        try {
            resultController = doExecute(context);
            resultController.setStatus("OK");
        } catch (IllegalArgumentException t) {
            resultController.setStatus("ERROR");
            resultController.setCode(3);
            resultController.setMessage(t.getMessage());
        }
//        catch (InvalidRequestException t) {
//            resultController.setStatus("ERROR");
//            resultController.setCode(2);
//            resultController.setMessage(t.getMessage());
//        }
        catch (Throwable t) {
            resultController.setStatus("ERROR");
            resultController.setCode(1);
            resultController.setMessage(t.getMessage());
        }
        response.setContentType("application/javascript");
        ServletOutputStream os = response.getOutputStream();
        try {
            jsonResult = JSON.toJSONString(resultController);
            os.print(jsonResult);
        } catch (Exception e) {
            response.setStatus(500);
            os.print(e.getMessage());
        } finally {
            os.flush();
        }
    }

    protected abstract ResultController doExecute(Map<String, Object> context) throws Exception;

}
