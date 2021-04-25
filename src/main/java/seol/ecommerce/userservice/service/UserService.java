package seol.ecommerce.userservice.service;

import seol.ecommerce.userservice.dto.UserDto;
import seol.ecommerce.userservice.jpa.UserEntity;

public interface UserService {

	UserDto createUser(UserDto userDto);

	UserDto getUserByUserId(String userId);

	Iterable<UserEntity> getUserByAll();

}
