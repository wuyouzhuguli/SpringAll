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

package com.alibaba.dubboadmin.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubboadmin.governance.service.UserService;
import com.alibaba.dubboadmin.governance.util.WebConstants;
import com.alibaba.dubboadmin.registry.common.domain.User;
import com.alibaba.dubboadmin.registry.common.util.Coder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginFilter implements Filter{

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static Pattern PARAMETER_PATTERN = Pattern.compile("(\\w+)=[\"]?([^,\"]+)[\"]?[,]?\\s*");
    private static final String BASIC_CHALLENGE = "Basic";
    private static final String DIGEST_CHALLENGE = "Digest";
    private static final String CHALLENGE = BASIC_CHALLENGE;
    private static final String REALM = User.REALM;

    @Autowired
    private UserService userService;
    private String logout = "/logout";
    private String logoutCookie = "logout";

    static Map<String, String> parseParameters(String query) {
        Matcher matcher = PARAMETER_PATTERN.matcher(query);
        Map<String, String> map = new HashMap<String, String>();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            map.put(key, value);
        }
        return map;
    }

    static byte[] readToBytes(InputStream in) throws IOException {
        byte[] buf = new byte[in.available()];
        in.read(buf);
        return buf;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (logger.isInfoEnabled()) {
            logger.info("AuthorizationValve of uri: " + req.getRequestURI());
        }
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (contextPath != null && contextPath.length() > 0 && !"/".equals(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (uri.equals(logout)) {
            if (!isLogout(req)) {
                setLogout(true, resp);
                showLoginForm(resp);
            } else {
                setLogout(false, resp);
                resp.sendRedirect(contextPath == null || contextPath.length() == 0 ? "/" : contextPath);
            }
            return;
        }
        User user = null;
        String authType = null;
        String authorization = req.getHeader("Authorization");
        if (authorization != null && authorization.length() > 0) {
            int i = authorization.indexOf(' ');
            if (i >= 0) {
                authType = authorization.substring(0, i);
                String authPrincipal = authorization.substring(i + 1);
                if (BASIC_CHALLENGE.equalsIgnoreCase(authType)) {
                    user = loginByBase(authPrincipal);
                } else if (DIGEST_CHALLENGE.equalsIgnoreCase(authType)) {
                    user = loginByDigest(authPrincipal, req);
                }
            }
        }
        if (user == null || user.getUsername() == null || user.getUsername().length() == 0) {
            showLoginForm(resp);
            return;
            //pipelineContext.breakPipeline(1);
        }
        if (user != null && StringUtils.isNotEmpty(user.getUsername())) {
            req.getSession().setAttribute(WebConstants.CURRENT_USER_KEY, user);
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }

    private void showLoginForm(HttpServletResponse response) throws IOException {
        if (DIGEST_CHALLENGE.equals(CHALLENGE)) {
            response.setHeader("WWW-Authenticate", CHALLENGE + " realm=\"" + REALM + "\", qop=\"auth\", nonce=\""
                + UUID.randomUUID().toString().replace("-", "") + "\", opaque=\""
                + Coder.encodeMd5(REALM) + "\"");
        } else {
            response.setHeader("WWW-Authenticate", CHALLENGE + " realm=\"" + REALM + "\"");
        }
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        response.setHeader("Content-Type", "text/html; charset=iso-8859-1");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private User getUser(String username) {
        return userService.findUser(username);
    }

    private User loginByBase(String authorization) {
        authorization = Coder.decodeBase64(authorization);
        int i = authorization.indexOf(':');
        String username = authorization.substring(0, i);
        if (username != null && username.length() > 0) {
            String password = authorization.substring(i + 1);
            if (password != null && password.length() > 0) {
                String passwordDigest = Coder.encodeMd5(username + ":" + REALM + ":" + password);
                User user = getUser(username);
                if (user != null) {
                    String pwd = user.getPassword();
                    if (pwd != null && pwd.length() > 0) {
                        if (passwordDigest.equals(pwd)) {
                            return user;
                        }
                    }
                }
            }
        }
        return null;
    }

    private User loginByDigest(String value, HttpServletRequest request) throws IOException {
        Map<String, String> params = parseParameters(value);
        String username = params.get("username");
        if (username != null && username.length() > 0) {
            String passwordDigest = params.get("response");
            if (passwordDigest != null && passwordDigest.length() > 0) {
                User user = getUser(username);
                if (user != null) {
                    String pwd = user.getPassword();
                    // A valid user, validate password
                    if (pwd != null && pwd.length() > 0) {
                        String uri = params.get("uri");
                        String nonce = params.get("nonce");
                        String nc = params.get("nc");
                        String cnonce = params.get("cnonce");
                        String qop = params.get("qop");
                        String method = request.getMethod();
                        String a1 = pwd;

                        String a2 = "auth-int".equals(qop)
                            ? Coder.encodeMd5(method + ":" + uri + ":" + Coder.encodeMd5(readToBytes(request.getInputStream())))
                            : Coder.encodeMd5(method + ":" + uri);
                        String digest = "auth".equals(qop) || "auth-int".equals(qop)
                            ? Coder.encodeMd5(a1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + a2)
                            : Coder.encodeMd5(a1 + ":" + nonce + ":" + a2);
                        if (digest.equals(passwordDigest)) {
                            return user;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isLogout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie != null && logoutCookie.equals(cookie.getName())) {
                    return "true".equals(cookie.getValue());
                }
            }
        }
        return false;
    }

    private void setLogout(boolean logoutValue, HttpServletResponse response) {
        response.addCookie(new Cookie(logoutCookie, String.valueOf(logoutValue)));
    }
}

