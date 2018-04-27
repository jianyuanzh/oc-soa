package cc.databus.course.service;

import cc.databus.course.mapper.CourseMapper;
import cc.databus.course.service.dto.CourseDTO;
import cc.databus.course.thrift.ServiceProvider;
import cc.databus.thrift.user.UserInfo;
import cc.databus.thrift.user.dto.TeacherDTO;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.thrift.TException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(interfaceClass = ICourseService.class)
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ServiceProvider serviceProvider;


    @Override
    public List<CourseDTO> courseList() {
        List<CourseDTO> courseDTOS = courseMapper.listCourses();
        if (courseDTOS != null) {
            for (CourseDTO dto : courseDTOS) {
                Integer teacherId = courseMapper.getCourseTeacher(dto.getId());
                if (teacherId != null) {
                    try {
                        UserInfo teacher = serviceProvider.getUserService().getTeacherById(teacherId);
                        dto.setTeacher(trans2Teacher(teacher));
                    }
                    catch (TException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return courseDTOS;
    }


    private TeacherDTO trans2Teacher(UserInfo userInfo) {
        TeacherDTO teacherDTO = new TeacherDTO();
        BeanUtils.copyProperties(userInfo, teacherDTO);
        return teacherDTO;
    }
}
