package com.ossant.photoapp.api.users.service;

import com.ossant.photoapp.api.users.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDto);

    UserDto getUserDetailsByEmail(String email);

}
