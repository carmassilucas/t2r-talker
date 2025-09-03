package chat.talk_to_refugee.ms_talker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsTalkerApplicationTests {

	@Test
	@DisplayName("Aplicação deve iniciar sem lançar exceções")
	void context_loads() {
		MsTalkerApplication.main(new String[] {});
	}

}
