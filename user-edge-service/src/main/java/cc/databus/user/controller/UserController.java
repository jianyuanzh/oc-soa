package cc.databus.user.controller;

import cc.databus.thrift.user.dto.UserDTO;
import cc.databus.thrift.user.UserInfo;
import cc.databus.user.redis.RedisClient;
import cc.databus.user.response.LoginResponse;
import cc.databus.user.response.Response;
import cc.databus.user.thrift.ServiceProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final Response SUCCESS = new Response();

    public static final Response USERNAME_PASSWORD_INVALID = new Response(1001, "username or password not correct.");

    public static final Response MOBILE_OR_EMAIL_REQUIRED = new Response(1002, "email or mobile is required.");

    public static final Response SEND_VERIFYCODE_FAILED = new Response(1003, "send verify code failed.");

    public static final Response VERIFY_CODE_INVALID = new Response(1004, "verify code invalid.");

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private RedisClient redisClient;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestParam("username") String username,
                          @RequestParam("password") String password) {
        try {
            UserInfo userInfo = serviceProvider.getUserService().getUserByName(username);
            if (userInfo == null) {
                return USERNAME_PASSWORD_INVALID;
            }
            else if (!userInfo.password.equalsIgnoreCase(md5(password))) {
                return USERNAME_PASSWORD_INVALID;
            }


            // 2. generate token
            String token = genToken();
            // 3. cache user token

            redisClient.set(token, toDTO(userInfo).toString(), 3600);

            return new LoginResponse(token);
        }
        catch (TException e) {
            e.printStackTrace();
            return USERNAME_PASSWORD_INVALID;
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginHtml() {
        return "login";
    }

    @ResponseBody
    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
    public Response sendVerifyCode(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "email", required = false) String email) {
        String message = "Verify code is: ";
        String code = randomCode("0123456789", 6);

        try {
            boolean result = false;
            if (StringUtils.isNotBlank(mobile)) {
                result = serviceProvider.getMessageService().sendMobileMessage(mobile, message + code);
                redisClient.set(mobile, code);
            }
            else if (StringUtils.isNotBlank(email)) {
                result = serviceProvider.getMessageService().sendEmailMessage(email, message + code);
                redisClient.set(email, code);
            }
            else {
                return MOBILE_OR_EMAIL_REQUIRED;
            }

            return result ? SUCCESS : SEND_VERIFYCODE_FAILED;
        }
        catch (TException e) {
            e.printStackTrace();
            return Response.exception(e);
        }

    }

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Response register(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "mobile", required = false) String mobile,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam("verifyCode") String verifyCode) {
        if (StringUtils.isBlank(mobile) && StringUtils.isBlank(email)) {
            return MOBILE_OR_EMAIL_REQUIRED;
        }

        if (StringUtils.isNotBlank(mobile)) {
            // 手机校验码
            String redisCode = redisClient.get(mobile);
            if (!verifyCode.equalsIgnoreCase(redisCode)) {
                return VERIFY_CODE_INVALID;
            }
        }
        else if (StringUtils.isNotBlank(email)) {
            String redisCode = redisClient.get(email);
            if (!verifyCode.equals(redisCode)) {
                return VERIFY_CODE_INVALID;
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(md5(password));
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);

        try {
            serviceProvider.getUserService().registerUser(userInfo);
        }
        catch (TException e) {
            return Response.exception(e);
        }

        return SUCCESS;
    }


    @ResponseBody
    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public UserDTO authentication(@RequestHeader("token") String token) {
        return redisClient.get(token);
    }

    private UserDTO toDTO(UserInfo userInfo) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userInfo, userDTO);
        return userDTO;
    }

    private String genToken() {
        return randomCode("01234567890abcdefghijklmnopqrstuvwxyz", 32);
    }

    private String randomCode(String seed, int size) {
        StringBuilder s = new StringBuilder(size);
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int loc = random.nextInt(seed.length());
            s.append(seed.charAt(loc));
        }

        return s.toString();
    }


    private String md5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(password.getBytes("utf-8"));
            return HexUtils.toHexString(md5Bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
