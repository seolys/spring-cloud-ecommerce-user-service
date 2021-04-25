package seol.userservice.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seol.userservice.dto.UserDto;
import seol.userservice.service.UserService;
import seol.userservice.vo.Greeting;
import seol.userservice.vo.RequestUser;
import seol.userservice.vo.ResponseUser;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

	//	private final Environment env;
	private final Greeting greeting;
	private final UserService userService;
	private final ModelMapper mapper;


	@GetMapping("/health_check")
	public String status() {
		return "It's working in User Service";
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

}
