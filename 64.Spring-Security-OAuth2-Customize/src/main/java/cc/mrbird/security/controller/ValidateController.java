package cc.mrbird.security.controller;

import cc.mrbird.security.service.RedisCodeService;
import cc.mrbird.security.validate.smscode.SmsCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ValidateController {

    @Autowired
    private RedisCodeService redisCodeService;

    @GetMapping("/code/sms")
    public void createSmsCode(HttpServletRequest request, HttpServletResponse response, String mobile) throws Exception {
        SmsCode smsCode = createSMSCode();
        redisCodeService.save(smsCode, new ServletWebRequest(request), mobile);
        // 输出验证码到控制台代替短信发送服务
        System.out.println("手机号" + mobile + "的登录验证码为：" + smsCode.getCode() + "，有效时间为120秒");
    }

    private SmsCode createSMSCode() {
        String code = RandomStringUtils.randomNumeric(6);
        return new SmsCode(code);
    }

}
