package com.ashuboi.photoappapiusers.photoappapiusers.Users.Service;

import com.ashuboi.photoappapiusers.photoappapiusers.Shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    UserDto getUserByEmail(String email);
}
