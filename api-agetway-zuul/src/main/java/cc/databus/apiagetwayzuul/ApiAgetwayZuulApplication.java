package cc.databus.apiagetwayzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class ApiAgetwayZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAgetwayZuulApplication.class, args);
	}
}
