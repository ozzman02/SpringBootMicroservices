package com.ossant.photoapp.api.users.service.impl;

import com.ossant.photoapp.api.users.dto.UserDto;
import com.ossant.photoapp.api.users.entity.User;
import com.ossant.photoapp.api.users.repository.UsersRepository;
import com.ossant.photoapp.api.users.service.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, ModelMapper modelMapper) {
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        User user = modelMapper.map(userDto, User.class);
        user.setEncryptedPassword("test");
        return modelMapper.map(usersRepository.save(user), UserDto.class);
    }
}
