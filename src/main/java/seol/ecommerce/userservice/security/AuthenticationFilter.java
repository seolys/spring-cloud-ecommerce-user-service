package seol.ecommerce.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import seol.ecommerce.userservice.service.UserService;
import seol.ecommerce.userservice.vo.RequestLogin;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private UserService userService;
	private Environment env;

	public AuthenticationFilter(AuthenticationManager authenticationManager,
			UserService userService,
			Environment env) {
		super.setAuthenticationManager(authenticationManager);
		this.userService = userService;
		this.env = env;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			RequestLogin credential = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(
							credential.getEmail(),
							credential.getPassword(),
							new ArrayList<>()
					)
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
//		super.successfulAuthentication(request, response, chain, authResult);
	}

}
