package seol.ecommerce.userservice.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seol.ecommerce.userservice.dto.UserDto;
import seol.ecommerce.userservice.jpa.UserEntity;
import seol.ecommerce.userservice.service.UserService;
import seol.ecommerce.userservice.vo.Greeting;
import seol.ecommerce.userservice.vo.RequestUser;
import seol.ecommerce.userservice.vo.ResponseUser;

@Validated
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user-service") // NOTE: api-gateway에서 url 포워딩방식 변경.
@RequestMapping("/")
public class UserController {

	private final Environment environment;
	private final Greeting greeting;
	private final ModelMapper mapper;

	private final UserService userService;


	@GetMapping("/health_check")
	public String status() {
		return String.format("It's working in User Service on PORT %s", environment.getProperty("local.server.port"));
	}

	@GetMapping("/welcome")
	public String welcome() {
//		return env.getProperty("greeting.message");
		return greeting.getMessage();
	}

	@PostMapping("/users")
	public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser user) {
		UserDto userDto = mapper.map(user, UserDto.class);
		UserDto savedUserDto = userService.createUser(userDto);

		ResponseUser responseUser = mapper.map(savedUserDto, ResponseUser.class);

		return ResponseEntity
				.status(HttpStatus.CREATED) // NOTE: POST 요청에는 200(OK)이 아닌, 201(CREATED)를 반환하는것이 더 명확한 코드이다.
				.body(responseUser);
	}

	@GetMapping("/users")
	public ResponseEntity<List<ResponseUser>> getUsers() {
		Iterable<UserEntity> userList = userService.getUserByAll();

		List<ResponseUser> responseUsers = new ArrayList<>();
		userList.forEach(v -> {
			responseUsers.add(mapper.map(v, ResponseUser.class));
		});

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(responseUsers);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
		UserDto userDto = userService.getUserByUserId(userId);

		ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(responseUser);
	}

}
