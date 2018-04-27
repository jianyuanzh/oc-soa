package cc.databus.course;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubboConfiguration
@SpringBootApplication
public class CourseDubboServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseDubboServiceApplication.class, args);
	}
}
