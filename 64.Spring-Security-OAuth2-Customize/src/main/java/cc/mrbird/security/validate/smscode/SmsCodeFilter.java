package cc.mrbird.security.validate.smscode;

import cc.mrbird.security.service.RedisCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RedisCodeService redisCodeService;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase("/login/mobile", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (Exception e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new AuthenticationServiceException(e.getMessage()));
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws Exception {
        String smsCodeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobileInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");

        String codeInRedis = redisCodeService.get(servletWebRequest, mobileInRequest);

        if (StringUtils.isBlank(smsCodeInRequest)) {
            throw new Exception("验证码不能为空！");
        }
        if (codeInRedis == null) {
            throw new Exception("验证码已过期！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInRedis, smsCodeInRequest)) {
            throw new Exception("验证码不正确！");
        }
        redisCodeService.remove(servletWebRequest, mobileInRequest);

    }
}