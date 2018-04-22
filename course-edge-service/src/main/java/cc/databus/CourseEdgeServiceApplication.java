package cc.databus;

import cc.databus.filter.CourseFilter;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
@EnableDubboConfiguration
public class CourseEdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseEdgeServiceApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		CourseFilter courseFilter = new CourseFilter();
		filterRegistrationBean.setFilter(courseFilter);

		filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));

		return filterRegistrationBean;
	}
}
