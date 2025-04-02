package es.grupo18.jobmatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = "es.grupo18.jobmatcher")
@EnableSpringDataWebSupport
public class JobmatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobmatcherApplication.class, args);
	}

}
