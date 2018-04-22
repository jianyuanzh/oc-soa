package cc.databus;

import cc.databus.course.service.ICourseService;
import cc.databus.course.service.dto.CourseDTO;
import cc.databus.thrift.user.dto.UserDTO;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CourseController {

    @Reference
    private ICourseService courseService;

    @RequestMapping(value = "/courses", method = RequestMethod.GET)
    public List<CourseDTO> courseList(HttpServletRequest request) {
        UserDTO userDTO = (UserDTO) request.getAttribute("user");
        System.out.println("userDTO=" + userDTO);
        return courseService.courseList();
    }
}
