package seol.ecommerce.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seol.ecommerce.userservice.dto.UserDto;
import seol.ecommerce.userservice.service.UserService;
import seol.ecommerce.userservice.vo.RequestLogin;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private UserService userService;
	private Environment environment;

	public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment environment) {
		super.setAuthenticationManager(authenticationManager);
		this.userService = userService;
		this.environment = environment;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			RequestLogin credential = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
			log.debug("Login Process 1. Login Request Email={}", credential.getEmail());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					credential.getEmail(),
					credential.getPassword(),
					new ArrayList<>()
			);
			return getAuthenticationManager().authenticate(
					authentication
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult
	) throws IOException, ServletException {
		User principal = (User) authResult.getPrincipal();
		log.debug("Login Process 3. Login Success Username={}", principal.getUsername());
		UserDto userDetail = userService.getUserDetailsByEmail(principal.getUsername());

		String token = Jwts.builder()
				.setSubject(userDetail.getUserId())
				.setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
				.signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
				.compact();

		response.addHeader("token", token);
		response.addHeader("userId", userDetail.getUserId());
	}

}
