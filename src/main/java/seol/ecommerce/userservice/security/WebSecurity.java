package seol.ecommerce.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import seol.ecommerce.userservice.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final Environment environment;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserService userService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.authorizeRequests().antMatchers("/**")
//				.permitAll()
				.hasIpAddress("172.30.1.7")
				.and()
				.addFilter(getAuthenticationFilter())
				.csrf().disable();

		http.headers().frameOptions().disable(); // h2-console 사용을 위한 처리.
	}

	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter =
				new AuthenticationFilter(authenticationManager(), userService, environment);
		return authenticationFilter;
	}

	// select pwd from users where email=?
	// db_pwd(encrypted) == input_pwd(encrypted)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}

}
