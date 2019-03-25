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

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubboadmin.SpringUtil;
import com.alibaba.dubboadmin.governance.util.WebConstants;
import com.alibaba.dubboadmin.web.mvc.governance.ServicesController;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class RouterController {

    @Autowired
    ServicesController servicesController;

    private boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == Boolean.class || cls == Byte.class
            || cls == Character.class || cls == Short.class || cls == Integer.class
            || cls == Long.class || cls == Float.class || cls == Double.class
            || cls == String.class;
    }

    private Object convertPrimitive(Class<?> cls, String value) {
        if (cls == boolean.class || cls == Boolean.class) {
            return value == null || value.length() == 0 ? false : Boolean.valueOf(value);
        } else if (cls == byte.class || cls == Byte.class) {
            return value == null || value.length() == 0 ? 0 : Byte.valueOf(value);
        } else if (cls == char.class || cls == Character.class) {
            return value == null || value.length() == 0 ? '\0' : value.charAt(0);
        } else if (cls == short.class || cls == Short.class) {
            return value == null || value.length() == 0 ? 0 : Short.valueOf(value);
        } else if (cls == int.class || cls == Integer.class) {
            return value == null || value.length() == 0 ? 0 : Integer.valueOf(value);
        } else if (cls == long.class || cls == Long.class) {
            return value == null || value.length() == 0 ? 0 : Long.valueOf(value);
        } else if (cls == float.class || cls == Float.class) {
            return value == null || value.length() == 0 ? 0 : Float.valueOf(value);
        } else if (cls == double.class || cls == Double.class) {
            return value == null || value.length() == 0 ? 0 : Double.valueOf(value);
        }
        return value;
    }

    //address mapping
    @RequestMapping("/governance/addresses/{ip:[0-9.]+:?[0-9]+}/{type}")
    public String addressRouter(@PathVariable("ip") String ip, @PathVariable("type") String type,
                                HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("address", ip);
        return appRouter(null, "addresses", ip, type, request, response, model);
    }

    @RequestMapping("/governance/addresses/{ip:[0-9.]+:?[0-9]+}/{type}/{action}")
    public String addresswithIDRouter(@RequestParam Map<String, String> params, @PathVariable("ip") String ip, @PathVariable("type") String type,
                                      @PathVariable("action") String action, HttpServletRequest request,
                                      HttpServletResponse response, Model model) {
        model.addAttribute("address", ip);
        return appAction(params, null, "addresses",ip, type, action, request, response, model);
    }

    @RequestMapping("/governance/addresses/{ip:[0-9.]+:?[0-9]+}/{type}/{id}/{action}")
    public String addressWithIDandAction(@PathVariable("ip") String ip, @PathVariable("type") String type,
                                         @PathVariable("id") String id, @PathVariable("action") String action,
                                         HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("address", ip);
        return appActionWithIdandAction(null, null, type, id, action, request, response, model);
    }


    // service mapping
    @RequestMapping("/governance/services/{service}/{type}")
    public String servicerRouter(@PathVariable("service") String service, @PathVariable("type") String type,
                                 HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("service", service);
        return appRouter(null, "services", service, type, request, response, model);
    }

    @RequestMapping("/governance/services/{service}/{type}/{action}")
    public String serviceAction(@RequestParam Map<String, String> param,
                                @PathVariable("service") String service, @PathVariable("type") String type,
                                @PathVariable("action") String action, HttpServletRequest request,
                                HttpServletResponse response, Model model) {
        for (Map.Entry<String, String> entry : param.entrySet()) {
            System.out.println("key: " + entry.getKey());
            System.out.println("value: " + entry.getValue());
        }
        model.addAttribute("service", service);
        return appAction(param, null, "services", service, type, action, request, response, model);
    }

    @RequestMapping("/governance/services/{service}/{type}/{id}/{action}")
    public String serviceActionWithId(@RequestParam Map<String, Object> param,
                                      @PathVariable("service") String service,
                                      @PathVariable("type") String type, @PathVariable("id") String id,
                                      @PathVariable("action") String action, HttpServletRequest request, HttpServletResponse response, Model model) {
        String method = request.getMethod();
        String app = null;
        System.out.println("type: " + type);
        System.out.println("action: " + action);
        System.out.println("method: " + method);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getKey().equals("application")) {
                app = (String)entry.getValue();
            }
            System.out.println("key: " + entry.getKey());
            System.out.println("value: " + entry.getValue());
        }
        return appActionWithIdandAction(app, service, type, id, action, request, response, model);
    }

    // app mapping all execute goes here
    //@RequestMapping("/governance/applications/{app}/services/{ids}/{action}")
    //public String serviceActionWithApp(@PathVariable("app") String app, @PathVariable("ids") String ids,
    //                                   @PathVariable("type") String type, HttpServletRequest request,
    //                                   HttpServletResponse response, Model model) {
    //    return "";
    //}

    @RequestMapping("/governance/applications/{app}/{elements}/{element}/{type}")
    public String appRouter(@PathVariable("app") String app, @PathVariable("elements") String elements,
                            @PathVariable("element") String element, @PathVariable("type") String type,
                            HttpServletRequest request,
                            HttpServletResponse response, Model model) {
        if (app != null) {
            model.addAttribute("app", app);
        }
        if (StringUtils.isNumeric(element)) {
            //service action, shield, recover..
            Long[] ids = new Long[1];
            ids[0] = Long.valueOf(element);
            model.addAttribute("service", request.getParameter("service"));
            try {
                Method m = servicesController.getClass().getDeclaredMethod(type, Long[].class, HttpServletRequest.class,
                    HttpServletResponse.class, Model.class);
                Object result = m.invoke(servicesController, ids, request, response, model);
                return (String) result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (elements.equals("services")) {
            model.addAttribute("service", element);
        } else if (elements.equals("addresses")) {
            model.addAttribute("address", element);
        }

        String name = WebConstants.mapper.get(type);
        if (name != null) {
            Object controller = SpringUtil.getBean(name);
            if (controller != null) {
                try {
                    Method index =  controller.getClass().getDeclaredMethod("index", HttpServletRequest.class, HttpServletResponse.class,
                         Model.class);
                    Object result =  index.invoke(controller, request, response, model);
                    return (String)result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    @RequestMapping("/governance/applications/{app}/{type}")
    public String appWithService(@PathVariable("app") String app, @PathVariable("type") String type,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 Model model) {
        model.addAttribute("app", app);
        String name = WebConstants.mapper.get(type);
        if (name != null) {
            Object controller = SpringUtil.getBean(name);
            try {
                Method index = controller.getClass().getDeclaredMethod("index", HttpServletRequest.class,
                    HttpServletResponse.class, Model.class);
                Object result = index.invoke(controller, request, response, model);
                return (String) result;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return "";
    }


    @RequestMapping("/governance/applications/{app}/{elements}/{element}/{type}/{action}")
    public String appAction(@RequestParam Map<String, String> params, @PathVariable("app") String app,
                            @PathVariable("elements") String elements, @PathVariable("element") String element,
                            @PathVariable("type") String type, @PathVariable("action") String action,
                            HttpServletRequest request, HttpServletResponse response, Model model) {
        if (app != null) {
            model.addAttribute("app", app);
        }
        if (elements.equals("services")) {
            model.addAttribute("service", element);
        } else if (elements.equals("addresses")) {
            model.addAttribute("address", element);
        }

        String name = WebConstants.mapper.get(type);
        if (name != null) {
            Object controller = SpringUtil.getBean(name);
            if (controller != null) {
                if (request.getMethod().equals("POST")) {
                    Method[] methods = controller.getClass().getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName().equals(action)) {
                            Class<?> param = method.getParameterTypes()[0];
                            try {
                                if (!param.isAssignableFrom(HttpServletRequest.class)) {
                                    Object value = param.newInstance();
                                    Method[] mms = param.getDeclaredMethods();
                                    for (Method m : mms) {
                                        if (m.getName().toLowerCase().startsWith("set")) {
                                            String methodName = m.getName();
                                            String key = methodName.substring(3).toLowerCase();
                                            String tmp = params.get(key);
                                            Object obj = tmp;
                                            if (tmp != null) {
                                                Class<?> t = m.getParameterTypes()[0];
                                                if (isPrimitive(t)) {
                                                    obj = convertPrimitive(t, tmp);
                                                }
                                                m.invoke(value, obj);
                                            }

                                        }
                                    }
                                    return (String)method.invoke(controller, value, request, response, model);
                                } else {
                                    return (String)method.invoke(controller, request, response, model);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                } else {
                    try {
                        if (StringUtils.isNumeric(action)) {
                            // action is id, call show method
                            Method show =  controller.getClass().getDeclaredMethod("show", Long.class, HttpServletRequest.class, HttpServletResponse.class,
                                Model.class);
                            Object result =  show.invoke(controller, Long.valueOf(action), request, response, model);
                            return (String)result;
                        } else {
                            Method m = controller.getClass().getDeclaredMethod(action, HttpServletRequest.class,
                                HttpServletResponse.class,
                                Model.class);
                            Object result =  m.invoke(controller, request, response, model);
                            return (String)result;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }


    @RequestMapping("/governance/applications/{app}/services/{service}/{type}/{id}/{action}")
    public String appActionWithIdandAction(@PathVariable("app") String app, @PathVariable("service") String service,
                               @PathVariable("type") String type, @PathVariable("id") String id,
                               @PathVariable("action") String action,
                               HttpServletRequest request, HttpServletResponse response, Model model) {
        if (app != null) {
            model.addAttribute("app", app);
        }
        model.addAttribute("service", service);
        String name = WebConstants.mapper.get(type);
        if (name != null) {
            Object controller = SpringUtil.getBean(name);
            if (controller != null) {
                try {
                    Object result = null;
                    if (StringUtils.isNumeric(id)) {
                        //single id
                        Method m = null;
                        try {
                            m = controller.getClass().getDeclaredMethod(action, Long.class, HttpServletRequest.class,
                                HttpServletResponse.class, Model.class);
                            result = m.invoke(controller, Long.valueOf(id), request, response, model);
                        } catch (NoSuchMethodException e) {
                            m = controller.getClass().getDeclaredMethod(action, Long[].class, HttpServletRequest.class,
                                HttpServletResponse.class, Model.class);
                            result = m.invoke(controller, new Long[]{Long.valueOf(id)}, request, response, model);

                        }
                    } else {
                        //id array
                        String[] array = id.split(",");
                        Long[] ids = new Long[array.length];
                        for (int i = 0; i < array.length; i ++) {
                            ids[i] = Long.valueOf(array[i]);
                        }

                        Method m = controller.getClass().getDeclaredMethod(action, Long[].class, HttpServletRequest.class,
                            HttpServletResponse.class, Model.class);

                        result = m.invoke(controller, ids, request, response, model);
                    }
                    return (String)result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";

    }
}
