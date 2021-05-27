package seol.ecommerce.userservice.service;

import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import seol.ecommerce.userservice.client.OrderServiceClient;
import seol.ecommerce.userservice.dto.UserDto;
import seol.ecommerce.userservice.jpa.UserEntity;
import seol.ecommerce.userservice.jpa.UserRepository;
import seol.ecommerce.userservice.vo.ResponseOrder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final ModelMapper mapper;
	private final BCryptPasswordEncoder passwordEncoder;
	private final Environment environment;
	//	private final RestTemplate restTemplate;
	private final OrderServiceClient orderServiceClient;
	private final UserRepository userRepository;


	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());

		UserEntity userEntity = mapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

		UserEntity savedUserEntity = userRepository.save(userEntity);

		UserDto savedUserDto = mapper.map(savedUserEntity, UserDto.class);
		return savedUserDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		Optional<UserEntity> findUser = userRepository.findByUserId(userId);
		if (findUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}

		UserDto userDto = mapper.map(findUser.get(), UserDto.class);

		// Using as rest template
//		String orderUrl = String.format(environment.getProperty("order-service.url"), userId);
//		ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//				new ParameterizedTypeReference<List<ResponseOrder>>() {
//				});
//		List<ResponseOrder> orders = orderListResponse.getBody();

		// Using a feign client
		List<ResponseOrder> orders = null;
		try {
			orders = orderServiceClient.getOrders(userId);
		} catch (FeignException ex) {
			log.error("getOrders Error", ex);
		}

		userDto.setOrders(orders);

		return userDto;
	}

	@Override
	public Iterable<UserEntity> getUserByAll() {
		return userRepository.findAll();
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		Optional<UserEntity> findUser = userRepository.findByEmail(email);
		if (findUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}

		return mapper.map(findUser.get(), UserDto.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> findUser = userRepository.findByEmail(username);
		if (log.isDebugEnabled()) {
			log.debug("Login Process 2. findUser={}", findUser.isPresent() ? findUser.get().getUserId() : null);
		}

		if (findUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}

		return new User(
				username,
				findUser.get().getEncryptedPwd(),
				true,
				true,
				true,
				true,
				new ArrayList<>());
	}
}
