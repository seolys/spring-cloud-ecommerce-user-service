package seol.userservice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import seol.userservice.dto.UserDto;
import seol.userservice.jpa.UserEntity;
import seol.userservice.jpa.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final ModelMapper mapper;

	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());

		UserEntity userEntity = mapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPwd("encrypted_dummy_password");

		UserEntity savedUserEntity = userRepository.save(userEntity);

		UserDto savedUserDto = mapper.map(savedUserEntity, UserDto.class);
		return savedUserDto;
	}

}
