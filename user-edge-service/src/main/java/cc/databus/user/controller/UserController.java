package cc.databus.user.controller;

import cc.databus.thrift.user.UserInfo;
import cc.databus.user.dto.UserDTO;
import cc.databus.user.redis.RedisClient;
import cc.databus.user.response.LoginResponse;
import cc.databus.user.response.Response;
import cc.databus.user.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.security.provider.MD5;

import java.security.MessageDigest;
import java.util.Random;

@RestController
public class UserController {

    public static final Response USERNAME_PASSWORD_INVALID = new Response(1001, "username or password not correct.");

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private RedisClient redisClient;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
