package cc.databus.user.controller;

import cc.databus.thrift.user.UserInfo;
import cc.databus.user.response.Response;
import cc.databus.user.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class UserController {

    public static final Response USERNAME_PASSWORD_INVALID = new Response(1001, "username or password not correct.");

    @Autowired
    private ServiceProvider serviceProvider;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response login(@RequestParam("username") String username,
                          @RequestParam("password") String password) {
        try {
            UserInfo userInfo = serviceProvider.getUserService().getUserByName(username);
            if (userInfo == null) {
                return USERNAME_PASSWORD_INVALID;
            }
            else if (!userInfo.password.equals(md5(password))) {
                return USERNAME_PASSWORD_INVALID;
            }


            // 2. generate token
            String token =
            // 3.

        }
        catch (TException e) {
            e.printStackTrace();
            return USERNAME_PASSWORD_INVALID;
        }
        return null;
    }

    private String genToken() {
        return randomCode("01234567890abcdefghijklmnopqrstuvwxyz", 32);
    }

    private String randomCode(String seed, int size) {
        StringBuilder s = new StringBuilder(size);
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int loc = random.nextInt(s.length());
            s.append(seed.charAt(loc));
        }

        return s.toString();
    }


    private static String md5(String pass) {
        return MD5Encoder.encode(pass.getBytes());
    }
}
