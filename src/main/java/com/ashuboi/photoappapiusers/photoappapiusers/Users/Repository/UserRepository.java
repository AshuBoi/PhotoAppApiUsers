package com.ashuboi.photoappapiusers.photoappapiusers.Users.Repository;

import com.ashuboi.photoappapiusers.photoappapiusers.Users.Data.UserEntity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
