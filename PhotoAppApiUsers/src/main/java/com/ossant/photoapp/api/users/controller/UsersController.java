package com.ossant.photoapp.api.users.controller;

import com.ossant.photoapp.api.users.dto.UserDto;
import com.ossant.photoapp.api.users.model.CreateUserRequestModel;
import com.ossant.photoapp.api.users.model.CreateUserResponseModel;
import com.ossant.photoapp.api.users.service.UsersService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment environment;

    private final UsersService usersService;

    private final ModelMapper modelMapper;

    @Autowired
    public UsersController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/status/check")
    public String status() {
        return "working on port " + environment.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestModel createUserRequestModel) {
        return new ResponseEntity<>(modelMapper.map(usersService.createUser(modelMapper.map(createUserRequestModel,
                        UserDto.class)), CreateUserResponseModel.class), HttpStatus.CREATED);
    }
}
