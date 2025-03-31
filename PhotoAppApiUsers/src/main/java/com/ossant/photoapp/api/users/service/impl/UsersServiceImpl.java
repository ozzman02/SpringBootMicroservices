package com.ossant.photoapp.api.users.service.impl;

import com.ossant.photoapp.api.users.dto.UserDto;
import com.ossant.photoapp.api.users.entity.User;
import com.ossant.photoapp.api.users.repository.UsersRepository;
import com.ossant.photoapp.api.users.service.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, ModelMapper modelMapper, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        return modelMapper.map(usersRepository.save(modelMapper.map(userDto, User.class)), UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        User user = usersRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User with email " + email + " not found");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepository.findByEmail(username);
        if (user == null) throw new UsernameNotFoundException("User with username " + username + " not found");
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

}
