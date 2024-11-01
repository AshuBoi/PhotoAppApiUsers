package com.ashuboi.photoappapiusers.photoappapiusers.Users.Service.Impl;

import com.ashuboi.photoappapiusers.photoappapiusers.Users.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import com.ashuboi.photoappapiusers.photoappapiusers.Shared.UserDto;
import com.ashuboi.photoappapiusers.photoappapiusers.Users.Data.UserEntity;
import com.ashuboi.photoappapiusers.photoappapiusers.Users.Service.UserService;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRespository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRespository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRespository = userRespository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDetails,UserEntity.class);

        userRespository.save(userEntity);

        return modelMapper.map(userEntity,UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = userRespository.findByEmail(email);
        if(userEntity == null) {throw new UsernameNotFoundException(email);}
        return new ModelMapper().map(userEntity,UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRespository.findByEmail(username);
        if(userEntity == null) throw new UsernameNotFoundException("User not found "+username);

        // This is spring security ka User which implements UserDetails
        // if we are using email verification then the last boolean valur will be false until user is verified
        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }
}
