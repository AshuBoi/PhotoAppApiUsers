package com.ashuboi.photoappapiusers.photoappapiusers.Users.ui.controller;


import com.ashuboi.photoappapiusers.photoappapiusers.Shared.UserDto;
import com.ashuboi.photoappapiusers.photoappapiusers.Users.Service.UserService;
import com.ashuboi.photoappapiusers.photoappapiusers.Users.ui.Model.CreateUserRequestModel;
import com.ashuboi.photoappapiusers.photoappapiusers.Users.ui.Model.CreateUserResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;


    @GetMapping("/status/check")
    public String status(){
        return "Working on port " + env.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequestModel userDetails){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);

        CreateUserResponse returnValue = modelMapper.map(createdUser, CreateUserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
}
