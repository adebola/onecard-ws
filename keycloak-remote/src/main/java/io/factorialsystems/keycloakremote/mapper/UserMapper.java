package io.factorialsystems.keycloakremote.mapper;

import io.factorialsystems.keycloakremote.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectUsers();
    User selectUserById(@Param("id") Integer id);
    User selectUserByEmail(@Param("email") String email);
}
