package cc.databus.filter;
import cc.databus.thrift.user.dto.UserDTO;
import cc.databus.user.client.LoginFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CourseFilter extends LoginFilter {

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response, UserDTO userDTO) {
        request.setAttribute("user", userDTO);
    }
}
