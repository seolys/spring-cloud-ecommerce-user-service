package seol.ecommerce.userservice;

import feign.Logger;
import feign.Logger.Level;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// FeignClients로 대체
//	@Bean
//	@LoadBalanced // 직접경로(ip/url) 대신 MicroService Name으로 호출할수 있도록 한다.
//	public RestTemplate getRestTemplate() {
//		return new RestTemplate();
//	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Level.FULL;
	}

//	@Bean
//	public FeignErrorDecoder getFeignErrorDecoder() {
//		return new FeignErrorDecoder();
//	}

}
