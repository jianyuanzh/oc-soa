package cc.databus.user.client;

import cc.databus.thrift.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class LoginFilter implements Filter {


    private static Cache<String, UserDTO> cache =
            CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(3, TimeUnit.MINUTES)
            .build();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // check if already login
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(token)) {


            UserDTO userDTO = null;
            userDTO = cache.getIfPresent(token);

            if (userDTO == null) {
                userDTO = requestUserInfo(token);
            }

            if (userDTO == null) {
                response.sendRedirect("http://127.0.0.1:8082/user/login");
            }
            cache.put(token, userDTO);
            login(request, response, userDTO);

        }


        filterChain.doFilter(request, response);
    }

    public abstract void login(HttpServletRequest request, HttpServletResponse response, UserDTO userDTO);

    @Override
    public void destroy() {

    }

    private UserDTO requestUserInfo(String token) {
        String url = "http://127.0.0.1:8082/user/authentication";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.addHeader("token", token);
        try {
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("request user info failed! StatusLine: " + response.getStatusLine());
            }

            String responseContent = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readValue(responseContent, UserDTO.class);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
