package chat.talk_to_refugee.ms_talker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsTalkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTalkerApplication.class, args);
	}

}
