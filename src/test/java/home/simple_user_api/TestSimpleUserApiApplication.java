package home.simple_user_api;

import org.springframework.boot.SpringApplication;

public class TestSimpleUserApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SimpleUserApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
